package com.flexsolution.authentication.oauth2.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by max on 12/7/17 .
 */
@Setter
@Getter
@ToString
public class UserMetadata {

    private String id;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String pictureUrl;

                        /*
                    {
  "emailAddress": "test@flex-solution.com",
  "firstName": "test first name",
  "id": "MUX4KjhJev",
  "lastName": "test last name",
  "location": {
    "country": {"code": "ua"},
    "name": "Ukraine"
  },
  "pictureUrl": "https://media.licdn.com/mpr/mprx/0_PKKbUcCRdDsMtoqF_Tu3RaYYdDVMtERb5Tu8adijJCcytQBb5MSLf9_ZeCRKnkNC6nS3R9GZR4UJ3UbmrR3Hu2CRu4UM3UQk5R3H0DiRu4BJ3UM7ka2iSJ7AsibvKDcfrhjeJ91tu7oKC0nH97bGsI"
}
                     */
}
