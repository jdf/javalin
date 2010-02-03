lsystem {
	vocabulary: {
		A: draw,
		B: draw
	},
	rules: {
		A -> B--A--B, 
		B -> A++B++A
	},
	start : -A,
	angle: 30
}