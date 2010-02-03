#define a 137.5

lsystem {
	vocabulary: {
		f: move,
		D: ellipse
	},
	ignore: +-,
	rules: {
		A(n) -> +(a)[f(n^0.5)D(1)]A(n+1)
	},
	axiom: A(0),
	angle: 5
}