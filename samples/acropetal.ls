lsystem {
	vocabulary: {
		a: draw,
		b: draw
	},
	ignore: +-,
	rules: {
		b < a -> b
	},
	axiom: b[+a]a[-a]a[+a]a,
	angle: 19
}