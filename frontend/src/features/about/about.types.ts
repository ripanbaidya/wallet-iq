export interface AppInfoResponse {
  name: string;
  version: string;
  buildNumber: string;
  serverTime: string;
  copyright: string;
  license: License;
  support: Support;
  social: Social;
}

export interface License {
  name: string;
  url: string;
}

export interface Support {
  email: string;
  workingHours: string;
}

export interface Social {
  github: string;
  linkedIn?: string;
  instagram?: string;
}