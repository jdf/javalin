lsystem {
	vocabulary: {
		L: draw,
		R: draw
	},
	rules: {
		L -> L+R++R-L--LL-R+,
		R -> -L+RR++R+L--L-R
	},
	axiom: L,
	angle: 60
}