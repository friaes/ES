describe('Assessment', () => {
    before(() => {
        cy.deleteAllButArs();
        cy.createDemoAssessments();
    });

    after(() => {
        cy.deleteAllButArs();
    });

    const REVIEW = "1234567890"

    it('volunteer tests activities view', () => {
        cy.demoVolunteerLogin();

        cy.visit('/volunteer/activities');

        // Verify that the table has 6 entries
        cy.get('[data-cy=volunteerActivitiesTable]').find('tbody tr')
            .should('have.length', 6);

        // Verify that the first row has for `name` "A1"
        cy.get('[data-cy=volunteerActivitiesTable]').find('tbody tr')
            .first().find('td').eq(0).should('contain', 'A1');

        // Evaluate a Institution (Open popup)
        cy.get('[data-cy=volunteerActivitiesTable]').find('tbody tr')
            .first().find('td').eq(9).find('[data-cy=addReviewButton]').click();

        cy.get('[data-cy=assessmentReviewInput]').type(REVIEW);

        cy.get('[data-cy=saveButton]').click();

        cy.logout();
    });

    it('member tests assessment view', () => {
        cy.demoMemberLogin();

        cy.visit('/member/assessments');

        // Verify that only one Assessment exists
        cy.get('[data-cy=institutionAssessmentsTable]').find('tbody tr')
            .should('have.length', 1);

        // Verify the correct review value
        cy.get('[data-cy=institutionAssessmentsTable]').find('tbody tr')
            .first().find('td').eq(1).should('contain', REVIEW);

        cy.logout();
    });
});