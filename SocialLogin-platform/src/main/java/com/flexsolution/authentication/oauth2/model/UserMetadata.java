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
    private String industry;
    private String headline;
    private String publicProfileUrl;
    private String summary;

    private Location location = new Location();


    /*
    "location": {
        "country": {"code": "ua"},
        "name": "Ukraine"
     }
     */
    @Setter
    @Getter
    @ToString
    public class Location {
        private String name;
    }


  /*
  "positions": {
    "_total": 2,
    "values": [
      {
        "company": {"name": "test"},
        "id": 1145998991,
        "isCurrent": true,
        "location": {},
        "title": "Test"
      },
      {
        "company": {"name": "test company"},
        "id": 1162140752,
        "isCurrent": true,
        "location": {
          "country": {
            "code": "ua",
            "name": "Ukraine"
          },
          "name": "Ukraine"
        },
        "startDate": {"year": 2016},
        "title": "Tester title"
      }
    ]
  }
}
 */
}
