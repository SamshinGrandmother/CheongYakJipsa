package me.synn3r.jipsa.core.api.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.synn3r.jipsa.core.api.member.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

	boolean existsMemberByEmail(String email);

	Member findByEmail(String email);

	Member findByUserId(String username);

	boolean existsMemberByUserId(String username);
}
