lsystem {
	vocabulary: {
		F: draw
	},
	rules: {
		F -> [
			.33 -> F[+F]F[-F]F,
			.33 -> F[+F]F,
			.34 -> F[-F]F 
		]
	},
	axiom: F,
	angle: 21.5
}