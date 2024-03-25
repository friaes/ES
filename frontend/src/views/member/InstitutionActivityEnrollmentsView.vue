<template>
  <v-card class="table">
    <div class="text-h3">{{ activity.name }}</div>
    <v-data-table
      :headers="headers"
      :items="enrollments"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
      data-cy="activityEnrollmentsTable"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
          <v-spacer />
          <v-btn
            color="primary"
            dark
            @click="getActivities"
            data-cy="getActivities"
            >Activities</v-btn
          >
        </v-card-title>
      </template>
      <template v-slot:[`item.participating`]="{ item }">
        <p>{{ isParticipating(item) }}</p>
      </template>
      <template v-slot:[`item.action`]="{ item }">
        <v-tooltip v-if="!isParticipating(item) && activity.numberOfParticipations < activity.participantsNumberLimit" bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              @click="selectParticipant(item)"
              v-on="on"
              >check
            </v-icon>
          </template>
          <span>Select Participant</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <participation-dialog
      v-if="currentEnrollment && ParticipationSelectionDialog"
      v-model="ParticipationSelectionDialog"
      :enrollment="currentEnrollment"
      v-on:make-participant="onMakeParticipant"
      v-on:close-participation-dialog="onCloseParticipationSelectionDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Activity from '@/models/activity/Activity';
import Enrollment from '@/models/enrollment/Enrollment';
import Participation from '@/models/participation/Participation';
import ParticipationSelectionDialog from '@/views/member/ParticipationSelectionDialog.vue';


@Component({
  components: {
    'participation-dialog': ParticipationSelectionDialog,
  },
})
export default class InstitutionActivityEnrollmentsView extends Vue {
  activity!: Activity;
  enrollments: Enrollment[] = [];
  participations: Participation[] = [];
  search: string = '';

  currentEnrollment: Enrollment  | null = null;
  ParticipationSelectionDialog: boolean = false;

  headers: object = [
    {
      text: 'Name',
      value: 'volunteerName',
      align: 'left',
      width: '30%',
    },
    {
      text: 'Motivation',
      value: 'motivation',
      align: 'left',
      width: '40%',
    },
    {
      text: 'Participating',
      value: 'participating',
      align: 'left',
      width: '20%',
    },
    {
      text: 'Application Date',
      value: 'enrollmentDateTime',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      sortable: false,
      width: '5%',
    },
  ];

  async created() {
    this.activity = this.$store.getters.getActivity;
    if (this.activity !== null && this.activity.id !== null) {
      await this.$store.dispatch('loading');
      try {
        this.enrollments = await RemoteServices.getActivityEnrollments(
          this.activity.id
        );
        this.participations = await RemoteServices.getParticipationsByActivityId(
          this.activity.id
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }
  }

  selectParticipant(enrollment: Enrollment) {
    this.currentEnrollment = enrollment;
    this.ParticipationSelectionDialog = true;
  }

  onCloseParticipationSelectionDialog() {
    this.currentEnrollment = null;
    this.ParticipationSelectionDialog = false;
  }

  async onMakeParticipant(participation: Participation) {
    this.participations.unshift(participation);
    this.ParticipationSelectionDialog = false;
    if (this.currentEnrollment != null) {
      this.currentEnrollment = null;
    }
  }

  async getActivities() {
    await this.$store.dispatch('setActivity', null);
    this.$router.push({ name: 'institution-activities' }).catch(() => {});
  }

  isParticipating(enrollment: Enrollment) {
    console.log(this.participations);
    return this.participations.some((p) => p.volunteerId === enrollment.volunteerId);
  }

}
</script>

<style lang="scss" scoped>
.date-fields-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.date-fields-row {
  display: flex;
  gap: 16px;
  margin-top: 8px;
}
</style>
