<template>
  <v-dialog v-model="dialog" persistent width="1300">
    <v-card>
      <v-card-title>
        <span class="headline">
          {{ 'Create Enrollment' }}
        </span>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" lazy-validation>
          <v-row>
            <v-col cols="12" sm="6" md="4">
              <v-text-field
                label="*Motivation"
                required
                v-model="selectedEnrollment.motivation"
                data-cy="nameInput"
              ></v-text-field>
            </v-col>
          </v-row>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
          color="blue-darken-1"
          variant="text"
          @click="$emit('close-enrollment-dialog')"
        >
          Close
        </v-btn>
        <v-btn
          color="blue-darken-1"
          variant="text"
          @click="createEnrollment()"
          data-cy="updateEnrollment"
        >
          Save
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Vue, Component, Prop, Model } from 'vue-property-decorator';
import Enrollment from '@/models/enrollment/Enrollment';
import RemoteServices from '@/services/RemoteServices';
import Activity from '@/models/activity/Activity';
import { ISOtoString } from '@/services/ConvertDateService';

@Component({
  methods: { ISOtoString },
})
export default class EnrollmentDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Enrollment, required: true }) readonly enrollment!: Enrollment;
  @Prop({ type: Activity, required: true }) readonly activity!: Activity;

  selectedEnrollment: Enrollment = new Enrollment();
  selectedActivity: Activity = new Activity();

  isMotivationValid(value: any) {
    if (value && value.length >= 10) {
      return true;
    }
    return false;
  }

  async created() {
    this.selectedEnrollment = new Enrollment(this.enrollment);
    this.selectedActivity = new Activity(this.activity);
    console.log(this.selectedActivity);
    console.log(this.selectedEnrollment);
  }

  async createEnrollment() {
    if ((this.$refs.form as Vue & { validate: () => boolean }).validate()) {
      try {
        if (
          this.selectedActivity.id &&
          this.isMotivationValid(this.selectedEnrollment.motivation) &&
          this.selectedEnrollment
        ) {
          const result = await RemoteServices.createEnrollment(
            this.selectedActivity.id,
            this.selectedEnrollment,
          );
          this.$emit('save-enrollment', result);
        }
      } catch (error) {
        console.log(error);
      }
    }
  }
}
</script>

<style scoped lang="scss"></style>
