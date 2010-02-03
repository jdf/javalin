"""This is a comment."""

lsystem(centralLength, baseWidth, tipWidth, baseAngle, tipAngle) {
	vocabulary: {
		f: move
	},
	rules: {
		P -> [S[l][r]B[L][R]D],
		S -> @PS(0)f(30),
		B -> ^(baseAngle)f(centralLength)^(tipAngle),
		D -> #(0,1,0)@PD(0,20),
		l -> +(90)f(baseWidth)@PC(0,0,0)+(90+atan(centralLength/baseWidth))
		     [|f(centralLength/3)@PC(0,1,0)-(90)^(baseAngle)f(baseWidth*2/3)@PC(0,1,1)]
		     [f(50)@PC(0,0,1)],
		r -> -(90)f(baseWidth)@PC(0,0,3)-(90+atan(centralLength/baseWidth))
		     [|f(centralLength/3)@PC(0,2,0)+(90)^(baseAngle)f(baseWidth*2/3)@PC(0,1,2)]
		     [f(50)@PC(0,0,2)],
		L -> +(90)f(tipWidth)@PC(0,3,0)+(90-atan(50/tipWidth))
		     [f(centralLength/3)@PC(0,2,0)+(90)^(tipAngle)f(tipWidth*2/3)@PC(0,2,1)]
		     [|f(30)@PC(0,3,1)],
		R -> -(90)f(tipWidth)@PC(0,3,3)-(90-atan(50/tipWidth))
		     [f(centralLength/3)@PC(0,2,3)-(90)^(tipAngle)f(tipWidth*2/3)@PC(0,2,2)]
		     [|f(30)@PC(0,3,2)]
	},
	axiom: P,
	angle: 18
}