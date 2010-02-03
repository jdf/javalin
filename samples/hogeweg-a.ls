lsystem {
	vocabulary: {
		F: draw
	},
	ignore: +-F,
	rules: {
		Q < Q > Q -> Q,
		Q < Q > P -> P[+FPFP],
		Q < P > Q -> P,
		Q < P > P -> P,
		P < Q > Q -> Q,
		P < Q > P -> 1F1,
		P < P > Q -> Q,
		P < P > P -> Q,
		* < + > * -> -,
		* < - > * -> +
	},
	axiom: F1F1F1,
	angle: 22.5
}