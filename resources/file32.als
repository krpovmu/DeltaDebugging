/**
 * Relational logic revision exercises based on a simple model of a
 * file system trash can.
 *
 * The model has 3 unary predicates (sets), File, Trash and
 * Protected, the latter two a sub-set of File. There is a binary
 * predicate, link, a sub-set of File x File.
 *
 * Solve the following exercises using Alloy's relational logic, which
 * extends first-order logic with:
 *	- expression comparisons 'e1 in e2' and 'e1 = e2'
 *	- expression multiplicity tests 'some e', 'lone e', 'no e' and 'one e'
 *	- binary relational operators '.', '->', '&', '+', '-', ':>' and '<:'
 *	- unary relational operators '~', '^' and '*'
 *	- definition of relations by comprehension
 **/

/* The set of files in the file system. */
sig File {
  	/* A file is potentially a link to other files. */
	link : set File
}
/* The set of files in the trash. */
sig Trash in File {}
/* The set of protected files. */
sig Protected in File {}

/* The trash is empty. */
pred inv1 {
	no Trash
}

/* All files are deleted. */
pred inv2 {
	all f:File | f in Trash
}

/* Some file is deleted. */
pred inv3 {
	some Trash
}

/* Protected files cannot be deleted. */
pred inv4 {
	no Protected&Trash
}

/* All unprotected files are deleted.. */
pred inv5 {
  	not Protected in Trash
}

/* A file links to at most one file. */
pred inv6 {
	all f:File | lone f.link
}

/* There is no deleted link. */
pred inv7 {
	no link.Trash
}

/* There are no links. */
pred inv8 {
	no link
}

/* A link does not link to another link. */
pred inv9 {
	all f:File | no f.link.link
}

/* If a link is deleted, so is the file it links to. */
pred inv10 {

}
run {inv1 inv2 inv3 inv4 inv5 inv6 inv7 inv8 inv9 inv10} for 10
