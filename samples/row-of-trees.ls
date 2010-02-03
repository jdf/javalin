#define c 1
#define p 0.3
#define q (c-p)
#define h ((p*q)^0.5)

lsystem {
	vocabulary: {
		F: draw
	},
	rules: {
		F(x) -> F(x*p)+F(x*h)--F(x*h)+F(x*q) 
	},
	axiom: F(1),
	angle: 86
}