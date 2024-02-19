module houses
abstract sig House {
    major: one Major,
    color: one Color,
}
one sig H1, H2, H3 extends House {}

sig File { link: set File }
sig Trash in File {}
sig Protected in File {}

enum Major {
    Math, Phil, CS
}

enum Color {
    Blue, Red, White
}

fact {
    Major in House.major
    Color in House.color
    H2.major = Phil
    ((H1.color = Red and H2.major = Phil) or (H2.color = Red and H3.major = Phil))
    some h: House | h.color = Blue and h.major = CS
    H2.major = Math
}

run {}
