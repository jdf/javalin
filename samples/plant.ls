lsystem {
	vocabulary: {
		X: noop,
		F: draw
	},
	rules: {
		X -> F-[[X]+X]+F[+FX]-X,
		F -> FF
	},
	axiom: X,
	angle: 25
}