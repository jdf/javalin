""" 
	2D Hilbert curve 
"""
lsystem {
	vocabulary: {
		F: draw
	},
	rules: {
		L -> +RF - LFL - FR+,
		R -> -LF + RFR + FL-
	},
	axiom: L,
	angle: 90
}