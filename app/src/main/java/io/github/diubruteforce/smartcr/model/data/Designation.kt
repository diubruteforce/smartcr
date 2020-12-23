package io.github.diubruteforce.smartcr.model.data

enum class Designation(val title: String) {
    Dean("Dean"),
    AssociateDean("Associate Dean"),
    DepartmentHead("Department Head"),
    VisitingProfessor("Visiting Professor"),
    Professor("Professor"),
    AssociateProfessor("Associate Professor"),
    AssistantProfessor("Assistant Professor"),
    SeniorLecturer("Senior Lecturer"),
    LecturerSeniorScale("Lecturer (Senior Scale)"),
    Lecturer("Lecturer");

    override fun toString(): String {
        return title
    }
}