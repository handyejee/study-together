package helloworld.studytogether.answer.service;

import helloworld.studytogether.answer.entity.Answer;
import helloworld.studytogether.answer.dto.AnswerDTO;
import helloworld.studytogether.answer.repository.AnswerRepository;
import jakarta.persistence.EntityNotFoundException;
import helloworld.studytogether.questions.entity.Question;
import helloworld.studytogether.questions.repository.QuestionRepository;
import helloworld.studytogether.user.entity.User;
import helloworld.studytogether.user.entity.Role;
import jakarta.persistence.EntityNotFoundException;
import helloworld.studytogether.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    @Autowired
    public AnswerServiceImpl(AnswerRepository answerRepository,
        QuestionRepository questionRepository, UserRepository userRepository) {
        this.answerRepository = answerRepository;
      this.questionRepository = questionRepository;
      this.userRepository = userRepository;
    }
    @Override
    @Transactional
    public AnswerDTO createAnswer(AnswerDTO answerDTO) {
        // Question 객체 찾기 (questionId로)
        Question question = questionRepository.findById(answerDTO.getQuestionId())
            .orElseThrow(() -> new RuntimeException("Question not found with id: " + answerDTO.getQuestionId()));

        // User 객체 찾기 (userId로)
        User user = userRepository.findById(answerDTO.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found with id: " + answerDTO.getUserId()));

        // Answer 엔티티 생성 및 값 설정
        Answer answer = new Answer(
                question,
                user,
                answerDTO.getContent(),
                answerDTO.getImage(),
                answerDTO.getLikes(),
                answerDTO.isSelected()
        );

        // Answer 저장
        Answer savedAnswer = answerRepository.save(answer);

        // 저장된 엔티티를 DTO로 변환하여 반환
        return convertToDTO(savedAnswer);
    }

    @Override
    @Transactional(readOnly = true)
    public AnswerDTO getAnswerById(Long id) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found with id: " + id));
        return convertToDTO(answer);
    } //ID로 답변을 조회

    @Override
    @Transactional
    public AnswerDTO updateAnswer(Long id, AnswerDTO answerDTO) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found with id: " + id));

        // 'content' 필드 업데이트
        answer.updateContent(answerDTO.getContent());
        // 필요한 경우 다른 필드 업데이트
        // answer.updateImage(answerDTO.getImage()); // 이미지 업데이트용 메서드 추가 시

        Answer updatedAnswer = answerRepository.save(answer);
        return convertToDTO(updatedAnswer);
    }
    //기존 답변을 수정

    @Override
    @Transactional
    public void deleteAnswer(Long id) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found with id: " + id));
        answerRepository.delete(answer);
    }  //답변을 삭제

    private AnswerDTO convertToDTO(Answer answer) {
        AnswerDTO answerDTO = new AnswerDTO();
        answerDTO.setAnswerId(answer.getAnswerId());
        answerDTO.setContent(answer.getContent());
        answerDTO.setCreatedAt(answer.getCreatedAt());
        answerDTO.setUpdatedAt(answer.getUpdatedAt());
        // 필요한 경우, 다른 필드도 DTO에 설정
         answerDTO.setQuestionId(answer.getQuestionId().getQuestionId());
         answerDTO.setUserId(answer.getUser().getUserId());
        return answerDTO;
    }

    @Transactional
    @Override
    public void likeAnswer(Long answerId) {
        // 답변 조회
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("Answer not found with id: " + answerId));

        // '좋아요' 증가
        answer.incrementLikes();
        answerRepository.save(answer);
    }

    @Transactional
    @Override
    public void unlikeAnswer(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("Answer not found with id: " + answerId));

        // '좋아요' 감소
        answer.decrementLikes();
        answerRepository.save(answer);
    }

}  //엔티티를 dto로 변환하여 클라이언트와 데이터 전송 시 필요한 형식으로 변경