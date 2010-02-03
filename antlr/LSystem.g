/*
   Copyright 2008 Jonathan Feinberg

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
grammar LSystem;

@header {
	package javalin;
	import java.util.Set;
	import java.util.HashSet;
	import javalin.ops.*;
	import javalin.rules.*;
	import javalin.expr.*;
	import javalin.module.*;
}
@lexer::header {package javalin;}
@members {final LSystem lSys = new LSystem();}

lsystem	returns [LSystem value]:
	'lsystem' 
	( 
		'('
			p=LSYSPARAM { lSys.addParam($p.text); }
			(',' p=LSYSPARAM { lSys.addParam($p.text); })*
		')'
	)?
	'{'
		clause (',' clause)*
		','?	// permit sloppy trailing comma
	'}' 
	{ lSys.check(); $value = lSys; }
	;


clause:	
	  'vocabulary' ':' '{' vocab '}'
	| 'rules' ':' '{' rules '}'
	| 'angle' ':' quantity			{ lSys.setAngle( 2f * (float)Math.PI * $quantity.value / 360f ); }
	| 'axiom' ':' s=word			{ lSys.setAxiom($s.value); }
	| 'ignore' ':' ignore=symbols	{ lSys.setIgnore($ignore.text); }
	;	
		
vocab:	
	vocab_decl (',' vocab_decl)*
	','?	// permit sloppy trailing comma
	;
	
vocab_decl:	
	c=vocabulary_symbol ':' action { lSys.addAction( $c.value, $action.op ); }
	;
	
action returns [Op op]:
	  'move'	{ $op = Op.MOVE; }
	| 'draw'	{ $op = Op.DRAW; }
	| 'noop'	{ $op = Op.NOOP; }
	| 'ellipse' { $op = Op.ELLIPSE; }
	;


rules	:	r=rule_decl 
			{ lSys.addRule($r.rule); } 
			(',' r=rule_decl { lSys.addRule($r.rule); })* 
			','?	// permit sloppy trailing comma
			;
rule_decl returns [Rule rule]
	:	m=matcher '->' (
			s=successors { $rule = new SimpleRule(lSys, $m.matcher, $s.word); }
			|
			'[' { $rule = new StochasticRule(lSys, $m.matcher); }
			stochastic_rule[$rule] (',' stochastic_rule[$rule])* ']'
		)
	;

stochastic_rule[Rule rule]:	
	p=quantity '->' s=successors 
	{ ((StochasticRule)$rule).addProduction($p.value, $s.word); }
	;

successors returns [List<Successor> word]:	
	{ $word = new ArrayList<Successor>(); }
	(successor[$word])*
	;

push returns [ConstantModule m]:
	'[' { $m = ConstantModule.PUSH; }
	;

pop returns [ConstantModule m]:
	']' { $m = ConstantModule.POP; }
	;

constant_module returns [ConstantModule m]:
		'{' { $m = ConstantModule.START_POLY; }
	|	'}' { $m = ConstantModule.END_POLY; }
	|	'.' { $m = ConstantModule.VERTEX; }
	|	'|'	{ $m = ConstantModule.TURN_AROUND; }
	| 	'@PS' '(' patchIndex=integer ')' 
		{$m = ConstantModule.createPatchInitModule($patchIndex.value); }
	|	'@PC' '(' patchIndex=integer ',' row=integer ',' col=integer ')'
		{$m = ConstantModule.createPatchControlModule($patchIndex.value, $row.value, $col.value); }
	|	'@PD' '(' patchIndex=integer ',' gridSteps=integer (',' integer)? ')'
		{$m = ConstantModule.createPatchDrawModule($patchIndex.value, $gridSteps.value); }
;


successor [List<Successor> word]
	:	dynamic_successor { $word.add($dynamic_successor.s); }
	|	push {$word.add(ConstantModule.PUSH);} 
		successor[$word]* 
		pop {$word.add(ConstantModule.POP);}
	|	m=constant_module { $word.add(m); }
	;

dynamic_successor returns [DynamicSuccessor s]
	:
	c=symbol { $s = new DynamicSuccessor($c.text.charAt(0)); }
		expressions[s]?
	;
	
expressions [DynamicSuccessor s]:
	'(' 
	e=expr {$s.addExpression(ExpressionCompiler.compile(e));} 
	(',' e=expr {$s.addExpression(ExpressionCompiler.compile(e));})* 
	')'
	;
	
integer returns [int value]: 
	n=digits { $value = Integer.parseInt($n.text); }
	;

quantity returns [float value]: 
	n=number { $value = Float.parseFloat($n.text); }
	;

matcher	returns [Matcher matcher] 
@init{BooleanExpression condBool = BooleanExpression.ALWAYS_TRUE;}
	:
	(left=predecessor_word '<')? 
	pred=strict_predecessor 
	('>' right=predecessor_word)? 
	(':' ('*' | cond=expr {condBool = new AsBoolean(ExpressionCompiler.compile($cond.exp));} ))?
	{ $matcher = new Matcher($left.value, 
							 $pred.value, 
							 $right.value, 
							 condBool);
	}
	;

predecessor_word returns [List<IPredecessor> value]:	
	{ $value = new ArrayList<IPredecessor>(); }
	(p=predecessor { $value.add(p); })+
	;

predecessor returns [IPredecessor value]:	
	  '*' { $value = Predecessor.ANY; }
	| (m=push|m=pop|m=constant_module) { $value = $m.m; }
	| strict_predecessor { $value = $strict_predecessor.value; }
	;

strict_predecessor returns [Predecessor value]:
	symbol {$value = new Predecessor($symbol.text.charAt(0)) ;}
	params[$value]?
	;

params [Predecessor pred]:
	'(' 
		p1=PARAM {$pred.addParam($p1.text.charAt(0));} 
		(',' pn=PARAM {$pred.addParam($pn.text.charAt(0));})* 
	')'
	;
	
word returns [List<Module> value]:	
	{ $value = new ArrayList<Module>(); }
	modules[$value]
	;

modules [List<Module> word]:
	(
	   	(m=push|m=pop|m=constant_module) { $word.add($m.m); }
	   	| dynamic_module { $word.add($dynamic_module.value); }
	)+
	;
	
dynamic_module returns [DynamicModule value]
	:	symbol {$value = new DynamicModule($symbol.text.charAt(0));} values[$value]?
	;

values [DynamicModule m]:
	'(' 
		v=value { $m.addParam($v.value); } 
		(',' v=value { $m.addParam($v.value); } )* 
	')'
	;

value returns [DoubleValue value]
	:	q=quantity { $value = new DoubleValue.ConstantDoubleValue((double)$q.value); } 
	|	p=LSYSPARAM { $value = lSys.getParam($p.text); }
	;
		
expr returns [Exp exp]
	: 	e=compand { $exp = $e.exp; } 
		('|' e=compand { $exp = new Exp.Or($exp, $e.exp); })*
	;
	
compand returns [Exp exp]	
	:	e=comparison { $exp = $e.exp; } 
		('&' e=comparison { $exp = new Exp.And($exp, $e.exp); })*
	;

comparison returns [Exp exp]:	
	e=comparand { $exp = $e.exp; } 
	(op=comparison_op e=comparand {
		if ($op.text.equals("=")) $exp = new Exp.EQ($exp, $e.exp);
		else if ($op.text.equals(">")) $exp = new Exp.GT($exp, $e.exp);
		else if ($op.text.equals(">=")) $exp = new Exp.GE($exp, $e.exp);
		else if ($op.text.equals("<")) $exp = new Exp.LT($exp, $e.exp);
		else if ($op.text.equals("<=")) $exp = new Exp.LE($exp, $e.exp);
	})?;

comparison_op:	
	'>''='?|'<''='?|'='
	;
	
comparand returns [Exp exp]
	:	e=term   { $exp = $e.exp; }
		( op=comparand_op  e=term  {
			if ($op.text.equals("+")) $exp = new Exp.Add($exp, $e.exp);
			else if ($op.text.equals("-")) $exp = new Exp.Sub($exp, $e.exp);
		} )*
        ;
comparand_op	:	'+' | '-';
term returns [Exp exp]	:
			e=exponentiation { $exp = $e.exp; } 
			(op=termop  e=exponentiation {
				if ($op.text.equals("*")) $exp = new Exp.Mul($exp, $e.exp);
				else if ($op.text.equals("/")) $exp = new Exp.Div($exp, $e.exp);
				else if ($op.text.charAt(0) == '\%') $exp = new Exp.Mod($exp, $e.exp);
			})*
        ;
termop	:	'*'|'/'|'%';
exponentiation	returns [Exp exp]
	:	e=factor { $exp = $e.exp; }
		('^' e=factor { $exp = new Exp.BinaryMathMethod("pow", $exp, $e.exp); })*;

factor returns [Exp exp]
	: 
		q=quantity 			{ $exp = new Exp.Constant($q.value); }
	|	p=LSYSPARAM         { $exp = new Exp.Param($p.text); }
	|	PARAM				{ $exp = new Exp.Param($PARAM.text); }
	|	UFUN '(' e=expr ')' { $exp = new Exp.UnaryMathMethod($UFUN.text, $e.exp); }
	| 	'(' e=expr ')' 		{ $exp = $e.exp; }
	;

number	:	('+'|'-')? (decimal | digits decimal?);
decimal	:	'.' digits;
digits	:	DIGIT+;

symbols: symbol+;
symbol	:	CAP | DIGIT | PARAM | '+' | '-' | '&' | '^' | '/' | '\\' | '#' | ';';

vocabulary_symbol returns [char value] 
	:	vocabulary_symbol_text 
		{ $value = $vocabulary_symbol_text.text.charAt(0); }	
	;
vocabulary_symbol_text	:	CAP | DIGIT | PARAM;

fragment UFUN: 
	'sin' | 'sinh' | 'cos' | 'cosh' 
	| 'tan' | 'tanh' 
	| 'log' | 'floor' | 'ceil'
	| 'asin' | 'acos' | 'atan' ;	

fragment LSYSPARAM
	:	('a'..'z'|'A'..'Z')('a'..'z'|'A'..'Z')+
	;
	
PARAM : (UFUN '(') => UFUN { $type = UFUN; } 
	| (('a'..'z')('a'..'z')+) => LSYSPARAM { $type = LSYSPARAM; }
	| 'a'..'z';

CAP 
	:	(('A'..'Z')('A'..'Z'|'a'..'z')+) => LSYSPARAM { $type = LSYSPARAM; }
	| 'A'..'Z' ;

COMMENT
	:	'"""' (~'"' | '"'~'"' | '""'~'"')* '"""'
		{skip();}
	;


DIGIT: '0'..'9';

WS	:	(' '|'\t'|'\r'|'\n')+ {skip();} ;

