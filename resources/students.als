// Entities
sig Student {
    attends: set Course,
    friends: set Student
}

sig Course {
    enrolled: set Student
}

// Some specific students and courses
one sig Alice, Bob, Charlie extends Student {}
one sig Algebra, Biology, Chemistry extends Course {}

// Relationships and constraints
fact StudentCourseRelations {
    // Define friendships
    Alice.friends = Bob + Charlie
    Bob.friends = Alice
    Charlie.friends = Alice + Bob

    // Assign courses to students
    Algebra.enrolled = Alice + Charlie
    Biology.enrolled = Bob + Charlie
    Chemistry.enrolled = Alice + Bob

    // Ensure students are enrolled in the courses they attend
    //all s: Student | s.attends in s.^friends.enrolled
}

// Introduce an unsatisfiable condition
fact UnsatisfiableCondition {
    // Alice attends Algebra and Chemistry but is not enrolled in Chemistry
    Alice.attends = Algebra
    Alice.attends = Chemistry
    not (Alice in Chemistry.enrolled)
}

// Checking and visualization
run {} for 4 but exactly 3 Student, 3 Course
