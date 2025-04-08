package me.synn3r.jipsa.core.api.member.repository;

import me.synn3r.jipsa.core.api.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

  boolean existsMemberByEmail(String email);

  Member findByEmail(String email);
}
