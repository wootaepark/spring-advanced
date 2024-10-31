package org.example.expert.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class ApiAccessLogAspect {

	// 사용자 관리 API 포인트컷
	@Pointcut("execution(* org.example.expert.domain.user.controller.UserAdminController.*(..))")
	public void userAdminApi() {}

	// 댓글 관리 API 포인트컷
	@Pointcut("execution(* org.example.expert.domain.comment.controller.CommentAdminController.*(..))")
	public void commentAdminApi() {}

	// 사용자 관리 API 접근 로그
	@Before("userAdminApi()")
	public void logUserAdminAccess(JoinPoint joinPoint) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String userId = request.getHeader("User-ID");
		String requestBody = getRequestBody(joinPoint.getArgs());
		log.info("User Admin API Accessed: {}, User ID: {}, Request Time: {}, Request URL: {}, Request Body: {}",
			joinPoint.getSignature().getName(), userId, LocalDateTime.now(), request.getRequestURL(), requestBody);
	}

	// 댓글 관리 API 접근 로그
	@Before("commentAdminApi()")
	public void logCommentAdminAccess(JoinPoint joinPoint) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String userId = request.getHeader("User-ID");
		String requestBody = getRequestBody(joinPoint.getArgs());
		log.info("Comment Admin API Accessed: {}, User ID: {}, Request Time: {}, Request URL: {}, Request Body: {}",
			joinPoint.getSignature().getName(), userId, LocalDateTime.now(), request.getRequestURL(), requestBody);
	}

	// 성공적인 응답 로그 (사용자 관리)
	@AfterReturning(pointcut = "userAdminApi()", returning = "result")
	public void logUserAdminResponse(JoinPoint joinPoint, Object result) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String userId = request.getHeader("User-ID");
		log.info("User Admin API Response: {}, User ID: {}, Response: {}",
			joinPoint.getSignature().getName(), userId, result);
	}

	// 성공적인 응답 로그 (댓글 관리)
	@AfterReturning(pointcut = "commentAdminApi()", returning = "result")
	public void logCommentAdminResponse(JoinPoint joinPoint, Object result) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String userId = request.getHeader("User-ID");
		log.info("Comment Admin API Response: {}, User ID: {}, Response: {}",
			joinPoint.getSignature().getName(), userId, result);
	}

	// 요청 본문을 문자열로 가져오는 헬퍼 메서드
	private String getRequestBody(Object[] args) {
		if (args.length > 0) {
			for (Object arg : args) {
				if (arg instanceof String || arg instanceof UserRoleChangeRequest || arg instanceof ManagerSaveRequest) {
					return arg.toString();
				}
			}
		}
		return "No Request Body";
	}
}
