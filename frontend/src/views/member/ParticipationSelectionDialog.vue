<template>
  <v-dialog v-model="dialog" persistent width="600">
      <v-card>
      <v-card-title>
          <span class="headline">
          Select Participant
          </span>
      </v-card-title>
      <v-card-text>
          <v-form ref="form" lazy-validation>
          <v-row>
              <v-col cols="12">
              <v-text-field
                  label="Rating"
                  v-model="newParticipation.rating"
                  data-cy="rating"
                  :rules="[
                    (v) => isNumberValid(v) || 'Rating must be empty or between 1 and 5.',
                  ]"
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
          @click="$emit('close-participation-dialog')"
          >
          Close
          </v-btn>
          <v-btn
          color="blue-darken-1"
          variant="text"
          @click="makeParticipant"
          data-cy="makeParticipant"
          >
          Make Participant
          </v-btn>
      </v-card-actions>
      </v-card>
  </v-dialog>
  </template>
  
  <script lang="ts">
  import { Vue, Component, Prop, Model } from 'vue-property-decorator';
  import Participation  from '@/models/participation/Participation';
  import Enrollment  from '@/models/enrollment/Enrollment';
  import RemoteServices from '@/services/RemoteServices';
  
  @Component({
      components: {},
    })
    export default class ParticipationSelectionDialog extends Vue {
      @Model('dialog', Boolean) dialog!: boolean;
      @Prop({ type: Enrollment, required: true }) readonly enrollment!: Enrollment;
  
      newParticipation: Participation = new Participation();
      activityId: number | null = null;
  
      async created() {
      this.newParticipation = new Participation();
      this.activityId = this.enrollment.activityId as number;
      this.newParticipation.volunteerId =this.enrollment.volunteerId as number;
    }
    
      async makeParticipant() {
        if ((this.$refs.form as Vue & { validate: () => boolean }).validate()) {
          try {
            const result = await RemoteServices.createParticipationAsMember(
              this.activityId as number,
              this.newParticipation,
            );
            this.$emit('make-participant', result);
          } catch (error) {
            await this.$store.dispatch('error', error);
          }
        }
      }
  
      isNumberValid(value: any) {
      if (value.length == 0 || value == null) return true;
      if (!/^\d+$/.test(value)) return false;
      const parsedValue = parseInt(value);
      return parsedValue >= 1 && parsedValue <= 5;
    }
  }
    </script>
    
    <style scoped lang="scss"></style>