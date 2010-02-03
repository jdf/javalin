lsystem {
	vocabulary: {
		F: draw
	},
	rules: {
		X -> X+YF+,
		Y -> -FX-Y
	},
	axiom: FX,
	angle: 90
}