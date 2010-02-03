#define c 1
#define p 0.3
#define q (c-p)
#define h ((p*q)^0.5)

lsystem {
	vocabulary: {
		F: draw
	},
	rules: {
		F(x,t): t=0 -> F(x*p,2)+F(x*h,1)--F(x*h,1)+F(x*q,0),
		F(x,t): t>0 -> F(x, t-1) 
	},
	axiom: F(1,0),
	angle: 86
}