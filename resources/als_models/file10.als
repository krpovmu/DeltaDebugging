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
	all f:File | f not in Trash
}

/* All files are deleted. */
pred inv2 {
	all f:File | f in Trash
}

/* Some file is deleted. */
pred inv3 {
	some f:File | f in Trash
}

/* Protected files cannot be deleted. */
pred inv4 {
  all f:Protected | f not in Trash

}

/* All unprotected files are deleted.. */
pred inv5 {

  all f:File | f not in Protected implies f in Trash

}





/* A file links to at most one file. */
pred inv6 {

  all f,x,y : File | (f->x in link and f->y in link) implies x=y

}


pred isLink[f:File]{
	some g:File | g->f in link
}

/* There is no deleted link. */
pred inv7 {
	all f:File | isLink[f] implies f not in Trash
}

/* There are no links. */
pred inv8 {

}

/* A link does not link to another link. */
pred inv9 {

}

/* If a link is deleted, so is the file it links to. */
pred inv10 {

}
run {inv1 inv2 inv3 inv4 inv5 inv6 inv7 inv8 inv9 inv10} for 10
