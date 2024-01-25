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
pred attendsCourse(s: Student, c: Course) {s in c.enrolled}

pred friendsWithAllInCourse(s: Student, c: Course) {all f: s.friends | f in c.enrolled}

pred attendsNoCourse(s: Student) {no s.attends}

pred attendsMultipleCourses(s: Student) {#s.attends > 1}

pred notFriendsWith(s1, s2: Student) {s2 not in s1.friends}

pred sharesAtLeastOneCourse(s1, s2: Student) {some c: Course | (s1 in c.enrolled and s2 in c.enrolled)}

// Assertions
assert TwoPredicatesUnsatisfiableCondition {
    some s: Student | (
        attendsCourse[s, Algebra] and
        attendsNoCourse[s] // This combination is unsatisfiable
    )
}

// Checking the assertion
check TwoPredicatesUnsatisfiableCondition for 5 but exactly 3 Student, 3 Course
