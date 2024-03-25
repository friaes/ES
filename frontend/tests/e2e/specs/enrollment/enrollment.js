describe('Enrollment', () => {
    beforeEach(() => {
        cy.deleteAllButArs();
        cy.createDemoForEnrollments();
    });

    afterEach(() => {
        cy.deleteAllButArs();
    });

    it('create enrollment', () => {
        const MOTIVATION = 'I want to escape and home!';

        cy.demoVolunteerLogin();

        cy.logout();

    });    

});    