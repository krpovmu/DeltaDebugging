sig Track {
	succs : set Track,
	signals : set Signal
}
sig Junction, Entry, Exit in Track {}

sig Signal {}
sig Semaphore, Speed extends Signal {}
// Specify the following properties
// You can check their correctness with the different commands and
// when specifying each property you can assume all the previous ones to be true

pred inv1 {/*The station has at least one entry and one exit*/ }
pred inv2 {/*Signals belong to one track*/}
pred inv3 {/*Exit tracks are those without successor*/}
pred inv4 {/*Entry tracks are those without predecessors*/}
// Conflicting  Every Entry must be an Exit
pred conf1 {all e: Entry | e in Exit}
pred inv5 {/*Junctions are the tracks with more than one predecessor*/}
pred inv6 {/*Entry tracks must have a speed signal*/}
pred inv7 {/*The station has no cycles*/}
pred inv8 {/*It should be possible to reach every exit from every entry*/}
pred inv9 {/*Tracks not followed by junctions do not have semaphores*/}
pred inv10 {/*Every track before a junction has a semaphore*/}
// Conflicting  Every Entry must be an Exit
pred conf2 {some e: Entry | e not in Exit}

run{inv1 inv2 inv3 inv4 conf1 inv5 inv6 inv7 inv8 inv9 inv10 conf2} for 10
