describe('Participation', () => {
    beforeEach(() => {
        cy.deleteAllButArs();
        cy.createParticipations();
    });

    it('create participations', () => {

        cy.demoMemberLogin();
        cy.intercept('GET', '/users/*/getInstitution').as('getInstitutions');
        cy.get('[data-cy="institution"]').click();
        cy.get('[data-cy="activities"]').click();
        cy.wait('@getInstitutions');
        cy.get('[data-cy="memberActivitiesTable"] tbody tr')
        .should('have.length', 2)
        .eq(0).children().eq(3).should('contain', '1')
        cy.logout();

    });
});
  