<template>
  <div>
    <v-card class="table">
      <v-data-table
        :headers="headers"
        :items="activities"
        :search="search"
        disable-pagination
        :hide-default-footer="true"
        :mobile-breakpoint="0"
        data-cy="volunteerActivitiesTable"
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
          </v-card-title>
        </template>
        <template v-slot:[`item.themes`]="{ item }">
          <v-chip v-for="theme in item.themes" v-bind:key="theme.id">
            {{ theme.completeName }}
          </v-chip>
        </template>
        <template v-slot:[`item.action`]="{ item }">
          <v-tooltip v-if="item.state === 'APPROVED'" bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                color="red"
                v-on="on"
                data-cy="reportButton"
                @click="reportActivity(item)"
                >warning</v-icon
              >
            </template>
            <span>Report Activity</span>
          </v-tooltip>
          <v-tooltip
            v-if="!isEnrolled(item) && isAplicationDeadlineValid(item)"
            bottom
          >
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                color="green"
                v-on="on"
                data-cy="applyButton"
                @click="createEnrollment(item)"
                >mdi-check-bold</v-icon
              >
            </template>
            <span>Apply for Activity</span>
          </v-tooltip>
          <v-tooltip v-if="canReview(item)" bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                color="blue"
                v-on="on"
                data-cy="addReviewButton"
                @click="addReview(item.institution)"
                >edit</v-icon
              >
            </template>
            <span>Add Review</span>
          </v-tooltip>
        </template>
      </v-data-table>
      <enrollment-dialog
        v-if="dialog && selectedEnrollment"
        v-model="dialog"
        :enrollment="selectedEnrollment"
        :activity="selectedActivity"
        v-on:save-enrollment="onSaveEnrollment"
        v-on:close-enrollment-dialog="onCloseEnrollmentDialog"
      />
      <add-review
        :current-item="this.currentInstitution"
        v-if="addAssessment"
        v-model="addAssessment"
        v-on:close-dialog="onCloseDialog"
        v-on:assessment-created="onAssessmentCreated"
      />
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Activity from '@/models/activity/Activity';
import VolunteerView from '@/views/volunteer/VolunteerAddReview.vue';
import { show } from 'cli-cursor';
import EnrollmentDialog from '@/views/volunteer/EnrollmentDialog.vue';
import Enrollment from '@/models/enrollment/Enrollment';
import Institution from '@/models/institution/Institution';
import Participation from '@/models/participation/Participation';
import Assessment from '@/models/assessment/Assessment';

@Component({
  components: {
    'enrollment-dialog': EnrollmentDialog,
  },
  methods: { show },
  components: {
    'add-review': VolunteerView,
  },
})
export default class VolunteerActivitiesView extends Vue {
  dialog: boolean = false;
  selectedEnrollment: Enrollment | null = null;
  selectedActivity: Activity | null = null;

  activities: Activity[] = [];
  participations: Participation[] = [];
  assessments: Assessment[] = [];
  currentInstitution: Institution | null = null;
  addAssessment: boolean = false;
  search: string = '';
  enrollments: any[] = [];
  headers: object = [
    {
      text: 'Name',
      value: 'name',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Region',
      value: 'region',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Participants',
      value: 'participantsNumberLimit',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Themes',
      value: 'themes',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Description',
      value: 'description',
      align: 'left',
      width: '30%',
    },
    {
      text: 'State',
      value: 'state',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Start Date',
      value: 'formattedStartingDate',
      align: 'left',
      width: '5%',
    },
    {
      text: 'End Date',
      value: 'formattedEndingDate',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Application Deadline',
      value: 'formattedApplicationDeadline',
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
    await this.$store.dispatch('loading');
    try {
      this.activities = await RemoteServices.getActivities();
      this.enrollments = await RemoteServices.getVolunteerEnrollments();
      this.participations = await RemoteServices.getVolunteerParticipations();
      this.assessments = await RemoteServices.getVolunteerAssessments();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  isAplicationDeadlineValid(activity: Activity) {
    const today = new Date();
    const deadline = new Date(activity.applicationDeadline);
    return deadline >= today;
  }

  isEnrolled(activity: Activity) {
    return this.enrollments.some((e) => e.activityId === activity.id);
  }

  createEnrollment(item: Activity) {
    this.dialog = true;
    this.selectedEnrollment = new Enrollment();
    this.selectedActivity = new Activity(item);
  }

  async onSaveEnrollment(enrollment: Enrollment) {
    this.dialog = false;
    this.enrollments.push(enrollment);
    this.selectedActivity = null;
    this.selectedEnrollment = null;
  }

  onCloseEnrollmentDialog() {
    this.dialog = false;
    this.selectedActivity = null;
    this.selectedEnrollment = null;
  }

  async reportActivity(activity: Activity) {
    if (activity.id !== null) {
      try {
        const result = await RemoteServices.reportActivity(
          this.$store.getters.getUser.id,
          activity.id,
        );
        this.activities = this.activities.filter((a) => a.id !== activity.id);
        this.activities.unshift(result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  addReview(institution: Institution) {
    this.addAssessment = true;
    this.currentInstitution = institution;
  }

  onCloseDialog() {
    this.addAssessment = false;
  }

  async onAssessmentCreated(assessment: Assessment) {
    this.assessments.push(assessment);
    this.addAssessment = false;
  }

  canReview(item: Activity) {
    if (!item.formattedEndingDate) return false;
    if (new Date() <= new Date(item.endingDate)) return false;
    return true;
  }
}
</script>

<style lang="scss" scoped></style>
