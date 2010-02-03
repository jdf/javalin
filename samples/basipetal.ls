lsystem {
	vocabulary: {
		a: draw,
		b: draw
	},
	ignore: +-,
	rules: {
		a > b -> b
	},
	axiom: a[+a]a[-a]a[+a]b,
	angle: 19
}