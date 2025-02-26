import { ISOtoString } from '@/services/ConvertDateService';

export default class Assessment {
  id: number | null = null;
  review!: string;
  reviewDate!: string;
  volunteerName: string | null = null;
  institutionId: number | null = null;

  constructor(jsonObj?: Assessment) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.review = jsonObj.review;
      this.reviewDate = ISOtoString(jsonObj.reviewDate);
      this.volunteerName = jsonObj.volunteerName;
      this.institutionId = jsonObj.institutionId;
    }
  }
}
