sig Workstation {
	workers : set Worker,
	succ : set Workstation
}
one sig begin, end in Workstation {}

sig Worker {}
sig Human, Robot extends Worker {}

abstract sig Product {
	parts : set Product	
}

sig Material extends Product {}

sig Component extends Product {
	workstation : set Workstation
}

sig Dangerous in Product {}
// Specify the following properties.
// You can check their correctness with the different commands and
// when specifying each property you can assume all the previous ones to be true.

// Workers are either human or robots
pred inv1 {
	all w: Worker | w in Human + Robot
}

// Every workstation has workers, and every worker works in exactly one workstation
pred inv2 {
	all ws: Workstation | some ws.workers
	all w: Worker | one w.~workers
}

// Every component is assembled in exactly one workstation
pred inv3 {
	all c: Component | one c.workstation
}

// Components must have parts, and materials have no parts
pred inv4 {
	all c: Component | some c.parts
	all m: Material | no m.parts
}

// Humans and robots cannot work together
pred inv5 {
	no ws: Workstation | some ws.workers & Human and some ws.workers & Robot
}

// Components cannot be their own parts
pred inv6 {
	no c: Component | c in c.^parts
}

// Components built of dangerous parts are also dangerous
pred inv7 {
	all c: Component | some c.parts & Dangerous => c in Dangerous
}

// Dangerous components cannot be assembled by humans
pred inv8 {
	no c: Component | c in Dangerous and some c.workstation.workers & Human
}

// The workstations form a single line between begin and end
pred inv9 {
	all disj ws1, ws2: Workstation | ws1 in ws2.^succ or ws2 in ws1.^succ or ws1 = ws2
	begin in Workstation - end.succ
	end in Workstation - begin.~succ
	no disj ws1, ws2: Workstation | ws1 in ws2.succ and ws2 in ws1.succ
}

// The parts of a component must be assembled before it in the production line
pred inv10 {
	all c: Component, p: c.parts | p.workstation in c.workstation.~succ
}

// Additional predicate to force unsatisfiability
pred contradiction {
	// Assume a component that is dangerous and must be assembled by humans, contradicting inv8
	some c: Component | c in Dangerous and all w: c.workstation.workers | w in Human
}


run { inv1 inv2 inv3 inv4 inv5 inv6 inv7 inv8 inv9 inv10 contradiction } for 10
