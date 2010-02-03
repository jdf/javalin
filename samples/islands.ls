lsystem {
	vocabulary: {
		F: draw,
		M: move
	},
	rules: {
		F -> F+M-FF+F+FF+FM+FF-M+FF-F-FF-FM-FFF,
		M -> MMMMMM
	},
	axiom: F+F+F+F,
	angle: 90
}