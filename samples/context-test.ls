lsystem {
	vocabulary: {
		a: noop,
		b: noop,
		c: noop
	},
	ignore: +-,
	rules: {
		b < a -> b,
		    b -> a
	},
	axiom: b-[cc]a+aaaaaaa,
	angle: 90
}