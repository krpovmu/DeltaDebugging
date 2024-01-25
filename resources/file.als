sig File {link : set File}
sig Trash in File {}
sig Protected in File {}

pred inv1 {all f:File|f not in Trash}
pred inv2 {all f:File|f in Trash}
pred inv3 {some f:File|f in Trash}
pred inv4 {all f:File|f in Protected implies f not in Trash}
//pred inv4 {all f:File|f in Protected implies f in Trash} // Modified inv4
pred inv7 {all f:File | isLink[f] implies f not in Trash}
pred isLink[f:File] {some g:File | g->f in link}
pred inv8 {all g:File | not isLink[g]}
pred inv8 {all g:File | isLink[g]}
pred inv5 {all f:File|f not in Protected implies f in Trash}
//pred inv5 {all f:File|f not in Protected implies f in Trash} // Modified inv5
pred inv6 {all x,y,z : File | (x->y in link and x->z in link) implies z=y}
pred inv9 {all f,g,h:File | f->g in link implies g->h not in link}
pred inv10 {all f:Trash,g:File | f->g in link implies g in Trash}

run {}
