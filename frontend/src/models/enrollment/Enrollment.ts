import { ISOtoString } from '@/services/ConvertDateService';

export default class Enrollment {
  id: number | null = null;
  motivation!: string;
  volunteerName!: string;
  enrollmentDateTime!: string;
  activityId: number | null = null;
  participating!: boolean;

  constructor(jsonObj?: Enrollment) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.motivation = jsonObj.motivation;
      this.volunteerName = jsonObj.volunteerName;
      this.enrollmentDateTime = ISOtoString(jsonObj.enrollmentDateTime);
      this.activityId = jsonObj.activityId;
      this.participating = jsonObj.participating;
    }
  }
}
