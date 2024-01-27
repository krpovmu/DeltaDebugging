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

pred friendsWith(s1, s2: Student) {s2 in s1.friends}

pred notFriendsWith(s1, s2: Student) {not s2 in s1.friends}

pred doesNotAttendCourse(s: Student, c: Course) {not s in c.enrolled}

pred attendsMoreCoursesThan(s1, s2: Student) {#s1.attends > #s2.attends}

pred attendsFewerCoursesThan(s1, s2: Student) {#s1.attends < #s2.attends}

// Run command to find an unsatisfiable instance
run {
    some s: Student | (
        attendsCourse[s, Algebra] and
        doesNotAttendCourse[s, Algebra]
    )
} for 5 but exactly 3 Student, 3 Course
