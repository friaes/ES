<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-dialog')"
    @keydown.esc="$emit('close-dialog')"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-form ref="form" lazy-validation>
        <v-card-title>
          <span class="headline">New Assessment</span>
        </v-card-title>

        <v-card-text class="text-left">
          <v-text-field
            v-model="assessment.review"
            label="Review"
            data-cy="assessmentReviewInput"
            :rules="[validInput]"
            required
          />
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn
            color="blue darken-1"
            @click="$emit('close-dialog')"
            data-cy="cancelButton"
            >Close
          </v-btn>
          <v-btn
            v-if="validReview"
            color="blue darken-1"
            @click="submit"
            data-cy="saveButton"
            >Save
          </v-btn>
        </v-card-actions>
      </v-form>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Assessment from '@/models/assessment/Assessment';
import Institution from '@/models/institution/Institution';

@Component({
  components: {},
})
export default class VolunteerAddReview extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ required: true, type: Object }) readonly currentItem!: Institution;

  assessment: Assessment = new Assessment();
  validReview: boolean = false;

  created() {}

  validInput(value: string | null | undefined): true | string {
    const inputValue = value || '';
    if (inputValue.trim().length >= 10) {
      this.validReview = true;
      return true;
    } else {
      this.validReview = false;
      return 'Review has to be at least 10 characters long';
    }
  }

  async submit() {
    if (this.currentItem.id == null) return;

    try {
      this.assessment = await RemoteServices.createAssessment(
        this.currentItem.id,
        this.assessment,
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>
