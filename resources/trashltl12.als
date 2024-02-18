/**
 * Linear temporal logic revision exercises based on a simple model of a
 * file system trash can.
 *
 * Solve the following exercises using Electrum's temporal logic, which
 * extends first-order relational logic with:
 *	- unary future operators after, always and eventually
 *	- binary future operators until and releases
 *	- unary past operators before, historically and once
 *	- binary past operators since and triggered
 *  - primed relational expression e'
 **/
var sig File {
	var link : lone File
}
var sig Trash in File {}
var sig Protected in File {}

// initially the trash is empty and there are no protected file
pred prop1 {
	no Trash && no Protected
}

// initially there are no files, but some are immediately created
pred prop2 {
	no File && some File

}

// there is always some file in the system
pred prop3 {

}

// some file will eventually be sent to the trash
pred prop4 {

}

// some file will eventually be deleted
pred prop5 {

}

// whenever a file is sent to the trash, it remains in there forever
pred prop6 {

}

// some file will be protected
pred prop7 {

}

// whenever a link exists, it will eventually be in the trash
pred prop8 {

}

// a protected file is at no time sent to the trash
pred prop9 {

}

// the protected status never changes
pred prop10 {

}

// every unprotected file becomes protected in the succeeding state
pred prop11 {

}

// a file will eventually be sent to the trash and remain there indefinitely
pred prop12 {

}

// if a file is ever in the trash, it was once outside
pred prop13 {

}

// whenever a protected file is in the trash, in the succeeding state it no longer is protected
pred prop14 {

}

// anytime a file exists, it will eventually be sent to the trash
pred prop15 {

}

// if a file is protected, it has always been so
pred prop16 {

}

// when a file is sent to the trash, it is deleted in the succeeding state
pred prop17 {

}

// protected files will only be deprotected if sent to the trash
pred prop18 {

}

// all protected files will be sent to the trash but remain protected until then
pred prop19 {

}

// whenever a file is in the trash, it has been so since it was deprotected
pred prop20 {

}
run {prop1 prop2 prop3 prop4 prop5 prop6 prop7 prop8 prop9 prop10 prop11 prop12 prop13 prop14 prop15 prop16 prop17 prop18 prop19 prop20} for 10
