#define R 1.456

lsystem {
	vocabulary: {
		F: draw,
		A: noop
	},
	rules: {
		A(s) -> F(s)[+A(s/R)][-A(s/R)],
	},
	axiom: +(90)A(1),
	angle: 89
}