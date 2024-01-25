// Entities
sig Student {
    attends: set Course,
    friends: set Student
}

sig Course {
    enrolled: set Student
}

// Specific instances
one sig Alice, Bob, Charlie extends Student {}
one sig Algebra, Biology, Chemistry extends Course {}

// Predicates
pred attendsCourse(s: Student, c: Course) {
    s in c.enrolled
}

pred friendsWithAllInCourse(s: Student, c: Course) {
    all f: s.friends | f in c.enrolled
}

pred attendsMoreCoursesThan(s1, s2: Student) {
    #s1.attends > #s2.attends
}

pred sharesNoCourse(s1, s2: Student) {
    no c: Course | (s1 in c.enrolled and s2 in c.enrolled)
}

pred attendsAtLeastOneCourse(s: Student) {
    some c: Course | s in c.enrolled
}

// Assertions
assert ComplexUnsatisfiableCondition {
    some s: Student | (
        attendsCourse[s, Algebra] and
        friendsWithAllInCourse[s, Biology] and
        attendsMoreCoursesThan[s, Bob] and
        sharesNoCourse[s, Charlie] and
        attendsAtLeastOneCourse[s]
    )
}

// Checking the assertion
check ComplexUnsatisfiableCondition for 5 but exactly 3 Student, 3 Course
