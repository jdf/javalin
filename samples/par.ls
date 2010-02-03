lsystem {
	vocabulary: {
		A: move,
		B: draw
	},
	rules: {
		A(x,y) :y<=3 -> A(x*2,x+y),
		A(x,y) : (y > 3) -> B(x)A(x/y, 0),
		B(x):x<1 -> C,
		B(x):x>=1 -> B(x-1)
	},
	axiom: B(2)A(4,4),
	angle: 22.5
}