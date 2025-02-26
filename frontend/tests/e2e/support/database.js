const credentials = {
  user: Cypress.env('psql_db_username'),
  host: Cypress.env('psql_db_host'),
  database: Cypress.env('psql_db_name'),
  password: Cypress.env('psql_db_password'),
  port: Cypress.env('psql_db_port'),
};

const INSTITUTION_COLUMNS = "institutions (id, active, confirmation_token, creation_date, email, name, nif, token_generation_date)";
const USER_COLUMNS = "users (user_type, id, creation_date, name, role, state, institution_id)";
const AUTH_USERS_COLUMNS = "auth_users (auth_type, id, active, email, username, user_id)";
const PARTICIPATION_COLUMNS = "participation (id, acceptance_date, rating, activity_id, volunteer_id)";
const ENROLLMENT_COLUMNS = "enrollment (id, enrollment_date_time, motivation, activity_id, volunteer_id)";
const ACTIVITY_COLUMNS = "activity (id, application_deadline, creation_date, description, ending_date, name, participants_number_limit, region, starting_date, state, institution_id)";

const now = new Date();
const tomorrow = new Date(now);
tomorrow.setDate(now.getDate() + 1);
const dayAfterTomorrow = new Date(now);
dayAfterTomorrow.setDate(now.getDate() + 2);
const yesterday = new Date(now);
yesterday.setDate(now.getDate() - 1);
const dayBeforeYesterday = new Date(now);
dayBeforeYesterday.setDate(now.getDate() - 2);

Cypress.Commands.add('deleteAllButArs', () => {
  cy.task('queryDatabase', {
    query: "DELETE FROM ASSESSMENT",
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "DELETE FROM ENROLLMENT",
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "DELETE FROM PARTICIPATION",
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "DELETE FROM ACTIVITY",
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "DELETE FROM AUTH_USERS WHERE NOT (username = 'ars')",
    credentials: credentials,
  });
  cy.task('queryDatabase', {
    query: "DELETE FROM USERS WHERE NOT (name = 'ars')",
    credentials: credentials,
  });
  cy.task('queryDatabase', {
    query: "DELETE FROM INSTITUTIONS",
    credentials: credentials,
  });
});


Cypress.Commands.add('createDemoEntities', () => {
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + INSTITUTION_COLUMNS + generateInstitutionTupleForAssessment(1, 'DEMO INSTITUTION', '000000000'),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + USER_COLUMNS + generateUserTuple(2, "MEMBER","DEMO-MEMBER", "MEMBER", 1),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + AUTH_USERS_COLUMNS + generateAuthUserTuple(2, "DEMO", "demo-member", 2),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + USER_COLUMNS + generateUserTuple(3, "VOLUNTEER","DEMO-VOLUNTEER", "VOLUNTEER", null),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + AUTH_USERS_COLUMNS + generateAuthUserTuple(3, "DEMO", "demo-volunteer", 3),
    credentials: credentials,
  })
});

Cypress.Commands.add('createDemoAssessments', () => {
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + INSTITUTION_COLUMNS + generateInstitutionTupleForAssessment(1, 'DEMO INSTITUTION', '000000000'),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + INSTITUTION_COLUMNS + generateInstitutionTupleForAssessment(2, 'DEMO INSTITUTION-2', '000000002'),
    credentials: credentials,
  })

  cy.task('queryDatabase',  {
    query: "INSERT INTO " + USER_COLUMNS + generateUserTuple(2, "MEMBER","DEMO-MEMBER", "MEMBER", 1),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + AUTH_USERS_COLUMNS + generateAuthUserTuple(2, "DEMO", "demo-member", 2),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + USER_COLUMNS + generateUserTuple(3, "VOLUNTEER","DEMO-VOLUNTEER", "VOLUNTEER", "NULL"),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + AUTH_USERS_COLUMNS + generateAuthUserTuple(3, "DEMO", "demo-volunteer", 3),
    credentials: credentials,
  })

  cy.task('queryDatabase',  {
    query: "INSERT INTO " + ACTIVITY_COLUMNS + generateActivityTuple(1, "Same institution is enrolled and participates", "A1", 1, "APPROVED", 1),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + ACTIVITY_COLUMNS + generateActivityTuple(2, "Same institution is enrolled and participates", "A2", 1, "APPROVED", 1),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + ACTIVITY_COLUMNS + generateActivityTuple(3, "Same institution is enrolled and does not participate", "A3", 2, "APPROVED", 1),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + ACTIVITY_COLUMNS + generateActivityTuple(4, "Same institution is not enrolled", "A4", 2, "APPROVED", 1),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + ACTIVITY_COLUMNS + generateActivityTuple(5, "Same institution before end date", "A5", 2, "APPROVED", 1),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + ACTIVITY_COLUMNS + generateActivityTuple(6, "Other institution is enrolled and participates", "A6", 3, "APPROVED", 2),
    credentials: credentials,
  })

  cy.task('queryDatabase',  {
    query: "INSERT INTO " + ENROLLMENT_COLUMNS + generateEnrollmentTuple(1, 1),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + ENROLLMENT_COLUMNS + generateEnrollmentTuple(2, 2),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + ENROLLMENT_COLUMNS + generateEnrollmentTuple(3, 3),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + ENROLLMENT_COLUMNS + generateEnrollmentTuple(4, 6),
    credentials: credentials,
  })

  cy.task('queryDatabase',  {
    query: "INSERT INTO " + PARTICIPATION_COLUMNS + generateParticipationTuple(1, 1, 3),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + PARTICIPATION_COLUMNS + generateParticipationTuple(2, 2, 3),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + PARTICIPATION_COLUMNS + generateParticipationTuple(3, 6, 3),
    credentials: credentials,
  })
})


//para criar a base de dados para o teste enrollment.js
Cypress.Commands.add('createDemoForEnrollments', () => {
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + INSTITUTION_COLUMNS + generateInstitutionTuple(1),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + USER_COLUMNS + generateUserTuple(2, "MEMBER","DEMO-MEMBER", "MEMBER", 1),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + AUTH_USERS_COLUMNS + generateAuthUserTuple(2, "DEMO", "demo-member", 2),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + USER_COLUMNS + generateUserTuple(3, "VOLUNTEER","DEMO-VOLUNTEER", "VOLUNTEER", "NULL"),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + AUTH_USERS_COLUMNS + generateAuthUserTuple(3, "DEMO", "demo-volunteer", 3),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + ACTIVITY_COLUMNS + generateActivityTupleforEnrollment(1, "Enrollment is open", "A1", 1, "APPROVED", 1),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + ACTIVITY_COLUMNS + generateActivityTupleforEnrollment(2, "Enrollment is open and it is already enrolled", "A2", 2, "APPROVED", 1),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + ACTIVITY_COLUMNS + generateActivityTupleforEnrollment(3, "Enrollment is closed", "A3", 3, "APPROVED", 1, false),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + ENROLLMENT_COLUMNS + "VALUES (5, '2024-02-06 18:51:37.595713', 'sql-inserted-motivation', 2, 3)",
    credentials: credentials,
  })
});


Cypress.Commands.add('createParticipations', () => {
  // Institution
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + INSTITUTION_COLUMNS + generateInstitutionTuple(1),
    credentials: credentials,
  });
  // Users
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + USER_COLUMNS + generateUserTuple(2, "MEMBER","DEMO-MEMBER", "MEMBER", 1, "2024-02-06 17:58:21.419878"),
    credentials: credentials,
  });
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + USER_COLUMNS + generateUserTuple(3, "VOLUNTEER","DEMO-VOLUNTEER", "VOLUNTEER", "NULL", "2024-02-06 17:58:23.732513"),
    credentials: credentials,
  });
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + USER_COLUMNS + generateUserTuple(4, "VOLUNTEER","DEMO-VOLUNTEER2", "VOLUNTEER", "NULL", "2024-02-06 17:58:23.732513"),
    credentials: credentials,
  });
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + USER_COLUMNS + generateUserTuple(5, "VOLUNTEER","DEMO-VOLUNTEER3", "VOLUNTEER", "NULL", "2024-02-06 17:58:23.732513"),
    credentials: credentials,
  });
  // Auth Users
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + AUTH_USERS_COLUMNS + generateAuthUserTuple(2, "DEMO", "demo-member", 2, "demo_member@mail.com"),
    credentials: credentials,
  });
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + AUTH_USERS_COLUMNS + generateAuthUserTuple(3, "DEMO", "demo-volunteer", 3, "demo_volunteer@mail.com"),
    credentials: credentials,
  });
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + AUTH_USERS_COLUMNS + generateAuthUserTuple(4, "DEMO", "demo-volunteer-2", 4, "demo_volunteer@mail.com"),
    credentials: credentials,
  });
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + AUTH_USERS_COLUMNS + generateAuthUserTuple(5, "DEMO", "demo-volunteer-3", 5, "demo_volunteer@mail.com"),
    credentials: credentials,
  });
  // Activities
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + ACTIVITY_COLUMNS + generateActivityTupleForPaticipation(1, "Has vacancies", "A1", 2, 1),
    credentials: credentials,
  });
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + ACTIVITY_COLUMNS + generateActivityTupleForPaticipation(2, "Has no vacancies", "A2", 1, 1),
    credentials: credentials,
  });
  // Enrollments
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + ENROLLMENT_COLUMNS + generateEnrollmentTupleForParticipation(1, "2024-02-06 18:51:37.595713", "Has vacancies and do not participate", 1, 3),
    credentials: credentials,
  });
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + ENROLLMENT_COLUMNS + generateEnrollmentTupleForParticipation(2, "2024-02-06 19:51:37.595713", "Has vacancies and participate", 1, 4),
    credentials: credentials,
  });
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + ENROLLMENT_COLUMNS + generateEnrollmentTupleForParticipation(3, "2024-02-06 18:51:37.595713", "Has no vacancies and participate", 2, 3),
    credentials: credentials,
  });
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + ENROLLMENT_COLUMNS + generateEnrollmentTupleForParticipation(4, "2024-02-06 20:51:37.595713", "Has no vacancies and do not participate", 2, 5),
    credentials: credentials,
  });
  //Participations
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + PARTICIPATION_COLUMNS + generateParticipationTuple(5, 1, 4),
    credentials: credentials,
  });
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + PARTICIPATION_COLUMNS + generateParticipationTuple(6, 2, 3),
    credentials: credentials,
  });

});

function constructEntry(...args) {
  let values = args.map(arg => arg === null ? "NULL" : `'${arg}'`).join(", ");
  values = values.replace(/'NULL'/g, "NULL");
  return ` VALUES (${values})`;
}


function generateAuthUserTuple(id, authType, username, userId) {
  return constructEntry(authType, id, "t", "demo_member@mail.com", username, userId);
}

function generateUserTuple(id, userType, name, role, institutionId) {
  return constructEntry(userType, id, "2022-02-06 17:58:21.419878", name, role, "ACTIVE", institutionId);
}

function generateInstitutionTuple(id) {
    return constructEntry(id, "t", "abca428c09862e89", "2024-02-06 17:58:21.402146", "demo_institution@mail.com", "DEMO INSTITUTION", "000000000", "2024-02-06 17:58:21.402134")
}

function generateInstitutionTupleForAssessment(id, name, nif) {
  return constructEntry(id, "t", "abca428c09862e89", "2024-02-06 17:58:21.402146", "demo_institution@mail.com", name, nif, "2024-02-06 17:58:21.402134")
}

function generateActivityTuple(id, description, name, participants_number_limit, state, institution_id) {
  return constructEntry(
      id, "2024-02-06 17:58:21.402146", "2024-02-06 17:58:21.402146",
      description, "2024-02-08 10:58:21.402146", name, participants_number_limit, "Lisbon",
      "2024-02-07 17:58:21.402146", state, institution_id
  );
}

function generateEnrollmentTuple(id, activity_id) {
  return constructEntry(id, "2024-02-06 18:51:37.595713", "sql-inserted-motivation", activity_id, "3");
}

//para criar a base de dados para o teste enrollment.js
function generateActivityTupleforEnrollment(id, discription, name, participants_number, state, institution_id, flag=true) {
  if (flag) {
    return "VALUES ('"
    + id + "', '2024-08-06 17:58:21.402146', '2024-08-06 17:58:21.402146','"
    + discription + "', '2024-08-08 17:58:21.402146', '"
    + name + "', '"
    + participants_number + "', 'Lisbon', '2024-08-07 17:58:21.402146', '"
    + state + "', '"
    + institution_id + "')";
  }
  return "VALUES ('"
    + id + "', '2024-02-06 17:58:21.402146', '2024-08-06 17:58:21.402146','"
    + discription + "', '2024-08-08 17:58:21.402146', '"
    + name + "', '"
    + participants_number + "', 'Lisbon', '2024-08-07 17:58:21.402146', '"
    + state + "', '"
    + institution_id + "')";
}

function generateEnrollmentTupleForParticipation(id, enrollment_date_time, motivation, activity_id, volunteer_id) {
  return "VALUES ('"
    + id + "', '"
    + enrollment_date_time + "', '"
    + motivation + "', '"
    + activity_id + "', '"
    + volunteer_id + "')";
}

function generateActivityTupleForPaticipation(id, description, name, participants_number_limit) {
  return "VALUES ('"
    + id + "', '2024-02-06 17:58:21.402146', '2024-01-06 17:58:21.402146', '"
    + description + "', '2024-02-08 17:58:21.402146', '"
    + name + "', '"
    + participants_number_limit + "', 'Lisbon', '2024-02-07 17:58:21.40214', 'APPROVED', '1')";
}

function generateParticipationTuple(id, activity_id, volunteer_id) {
  return "VALUES ('"
    + id + "', '2024-02-06 18:51:37.595713', '5', '"
    + activity_id + "', '"
    + volunteer_id + "')";
}