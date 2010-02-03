lsystem {
    vocabulary: {
        S: draw
    },
    rules: {
        S ->,
        6 -> PS++9S----7S[-PS----6S]++,
        7 -> +PS--9S[---6S--7S]+,
        P -> -6S++7S[+++PS++9S]-,
        9 -> --PS++++6S[+9S++++7S]--7S
    },
    axiom: [7]++[7]++[7]++[7]++[7],
    angle: 36
}