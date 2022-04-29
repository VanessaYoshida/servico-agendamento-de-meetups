package com.bootcamp.microservicemeetup.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Meetup {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column
  private String event;

  @OneToMany(mappedBy = "meetup", cascade = CascadeType.ALL)
  private List<Registration> registrations;

  @Column
  private String meetupDate;

  @Column
  private Boolean registered;

  @Column(name = "owner_id")
  private Integer ownerId;
}
