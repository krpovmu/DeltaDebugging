// Entities
sig Student {
    friends: set Student
}

sig Course {
    students: set Student
}

// Specific instances
one sig Alice, Bob, Charlie extends Student {}
one sig Algebra, Biology, Chemistry extends Course {}

// Predicates
pred attendsAllCourses(s: Student) {
    all c: Course | s in c.students
}

pred friendsAttendSameCourse(s: Student, c: Course) {
    all f: s.friends | f in c.students
}

// Assertions
assert UnsatisfiableModel {
    some s: Student | attendsAllCourses[s] and not friendsAttendSameCourse[s, Chemistry]
}

// Checking the assertion
check UnsatisfiableModel for 4 but exactly 3 Student, 3 Course
