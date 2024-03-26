describe('Enrollment', () => {
    beforeEach(() => {
        cy.deleteAllButArs();
        cy.createDemoForEnrollments();
    });

    afterEach(() => {
    });

    it('create enrollment', () => {
        const MOTIVATION = 'I want to escape and home!';

        cy.demoMemberLogin()
        // intercept get activities
        cy.intercept('GET', '/users/*/getInstitution').as('getInstitutions');
        // go to institution activities view
        cy.get('[data-cy="institution"]').click();
        cy.get('[data-cy="activities"]').click();
        cy.wait('@getInstitutions');

        // verifica se a tabela de atividades tem 3 atividades
        cy.get('[data-cy="memberActivitiesTable"] tbody tr')
            .should('have.length', 3)
            .eq(0)
            .children()
            .should('have.length', 12)

        // verifica se a primeira atividade Ã© a atividade 1 e tem 0 inscritos
        cy.get('[data-cy="memberActivitiesTable"] tbody tr')
            .eq(0).children().eq(0).should('contain', 'A1')
        cy.get('[data-cy="memberActivitiesTable"] tbody tr')
            .eq(0).children().eq(3).should('contain', '0')
        
        cy.logout();

        cy.demoVolunteerLogin();
        // intercept get activities request
        cy.intercept('GET', '/enrollments/volunteer').as('getEnrollmentsVolunteer');
        cy.intercept('POST', '/activities/*/enrollments').as('enroll');

        // go to volunteer activities view
        cy.get('[data-cy="volunteerActivities"]').click();
        // check request was done
        cy.wait('@getEnrollmentsVolunteer');

        cy.get('[data-cy="applyButton"]').click();

        cy.get('[data-cy="motivationInput"]').type(MOTIVATION);
        cy.get('[data-cy="updateEnrollment"]').click();
        // check request was done
        cy.wait('@enroll');

        cy.logout();

        cy.demoMemberLogin()

        cy.get('[data-cy="institution"]').click();
        cy.get('[data-cy="activities"]').click();
        cy.wait('@getInstitutions');

        // verifica se a tabela de atividades tem 3 atividades
        cy.get('[data-cy="memberActivitiesTable"] tbody tr')
            .should('have.length', 3)
            .eq(0)
            .children()
            .should('have.length', 12)

        // verifica se a primeira atividade Ã© a atividade 1 e tem 0 inscritos
        cy.get('[data-cy="memberActivitiesTable"] tbody tr')
            .eq(0).children().eq(0).should('contain', 'A1')
        cy.get('[data-cy="memberActivitiesTable"] tbody tr')
            .eq(0).children().eq(3).should('contain', '1')
            
        cy.intercept('GET', '/activities/*/enrollments').as('getEnrollments');

        cy.get('[data-cy="showEnrollments"]').eq(0).click();
        cy.wait('@getEnrollments');

        cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
            .should('have.length', 1)
            .eq(0)
            .children()
            .should('have.length', 2)

        cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
            .eq(0).children().eq(0).should('contain', MOTIVATION)
        
        cy.logout();    
    });    

});