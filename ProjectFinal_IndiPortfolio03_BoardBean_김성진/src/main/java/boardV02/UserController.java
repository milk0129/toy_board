package boardV02;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.File;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

@WebServlet("/UserController")
public class UserController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response); 
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        switch (action) {
            case "login":    handleLogin(request, response); break;
            case "logout":   handleLogout(request, response); break;
            case "signup":   handleSignUp(request, response); break;
            case "findId":   handleFindId(request, response); break;
            case "updateProfile": handleUpdateProfile(request, response); break;
            case "deleteUser": handleDeleteUser(request, response); break;

            case "loginForm": showLoginForm(request, response); break;
            case "signUpForm": showSignUpForm(request, response); break;
            case "findIdForm": showFindIdForm(request, response); break;
            case "userEditForm": showUserEditForm(request, response); break;
            case "deleteUserCheckForm": showUserDeleteForm(request, response); break;


            default:
                response.sendRedirect("/memV02DAO/memV02_01_BoardV02/boardFrame/login.jsp");
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String userId = request.getParameter("userId");
        String userPw = request.getParameter("userPw");

        UserDAO dao = new UserDAO();
        boolean valid = dao.loginCheck(userId, userPw);

        if (valid) {
            HttpSession session = request.getSession();
            System.out.println("로그인 성공 → 사용자 ID: " + userId);
            UserDTO loginUser = dao.getUser(userId);
            System.out.println("getUser() 결과: " + (loginUser == null ? "null" : loginUser.toString()));

            if (loginUser != null) {
                session.setAttribute("loginUser", loginUser);
                session.setAttribute("userId", loginUser.getUserId());
                session.setAttribute("userName", loginUser.getUserName());

                response.sendRedirect("BoardController?action=list");
            } else {
                request.setAttribute("msg", "로그인에는 성공했지만 사용자 정보를 불러올 수 없습니다.");
                request.setAttribute("location", request.getContextPath() + "/memV02DAO/memV02_01_BoardV02/boardFrame/login.jsp");
                RequestDispatcher rd = request.getRequestDispatcher("/memV02DAO/memV02_01_BoardV02/boardFrame/msgChk.jsp");
                rd.forward(request, response);
            }
        } else {
            request.setAttribute("msg", "아이디 또는 비밀번호가 틀렸습니다.");
            request.setAttribute("location", request.getContextPath() + "/memV02DAO/memV02_01_BoardV02/boardFrame/login.jsp");
            RequestDispatcher rd = request.getRequestDispatcher("/memV02DAO/memV02_01_BoardV02/boardFrame/msgChk.jsp");
            rd.forward(request, response);
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        response.sendRedirect("BoardController?action=list");
    }

    private void handleSignUp(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String userId = request.getParameter("userId");
        String userPw = request.getParameter("userPw");
        String userPwChk = request.getParameter("userPwChk"); 
        String userName = request.getParameter("userName");
        String userEmail = request.getParameter("userEmail");
        String userBirth = request.getParameter("userBirth");

        // 비밀번호와 확인이 일치하지 않으면
        if (!userPw.equals(userPwChk)) {
            request.setAttribute("msg", "❗ 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
            request.setAttribute("location", request.getContextPath() + "/memV02DAO/memV02_01_BoardV02/boardFrame/signUp.jsp");
            RequestDispatcher rd = request.getRequestDispatcher("/memV02DAO/memV02_01_BoardV02/boardFrame/msgChk.jsp");
            rd.forward(request, response);
            return;
        }

        UserDTO dto = new UserDTO();
        dto.setUserId(userId);
        dto.setUserPw(userPw);
        dto.setUserName(userName);
        dto.setUserEmail(userEmail);
        dto.setUserBirth(userBirth);
        dto.setProfileImg("default.png");

        UserDAO dao = new UserDAO();

        try {
            if (dao.isUserIdDuplicate(dto.getUserId())) {
                request.setAttribute("msg", "❗ 이미 사용 중인 아이디입니다. 다른 아이디를 입력해주세요.");
                request.setAttribute("location", request.getContextPath() + "/memV02DAO/memV02_01_BoardV02/boardFrame/signUp.jsp");

                RequestDispatcher rd = request.getRequestDispatcher("/memV02DAO/memV02_01_BoardV02/boardFrame/msgChk.jsp");
                rd.forward(request, response);
                return;
            }

            int result = dao.insertUser(dto);

            if (result > 0) {
                request.setAttribute("msg", dto.getUserName() + "님, 회원가입이 성공적으로 완료되었습니다!");
                request.setAttribute("location", request.getContextPath() + "/memV02DAO/memV02_01_BoardV02/boardFrame/login.jsp");

                RequestDispatcher rd = request.getRequestDispatcher("/memV02DAO/memV02_01_BoardV02/boardFrame/msgChk.jsp");
                rd.forward(request, response);
            } else {
                request.setAttribute("msg", "❗ 회원가입에 실패했습니다. 다시 시도해주세요.");
                request.setAttribute("location", request.getContextPath() + "/memV02DAO/memV02_01_BoardV02/boardFrame/signUp.jsp");
                RequestDispatcher rd = request.getRequestDispatcher("/memV02DAO/memV02_01_BoardV02/boardFrame/msgChk.jsp");
                rd.forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("msg", "❗ 예외 발생: " + e.getMessage());
            request.setAttribute("location", request.getContextPath() + "/memV02DAO/memV02_01_BoardV02/boardFrame/signUp.jsp");
            RequestDispatcher rd = request.getRequestDispatcher("/memV02DAO/memV02_01_BoardV02/boardFrame/msgChk.jsp");
            rd.forward(request, response);
        }
    }

    private void handleFindId(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("userName");
        String email = request.getParameter("userEmail");

        UserDAO dao = new UserDAO();
        String foundId = dao.findUserId(name, email);

        request.setAttribute("userName", name);
        request.setAttribute("userEmail", email);
        request.setAttribute("foundId", foundId);

        RequestDispatcher rd = request.getRequestDispatcher("/memV02DAO/memV02_01_BoardV02/boardFrame/findId.jsp");
        rd.forward(request, response);
    }
    
    private void handleDeleteUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userId = request.getParameter("userId");
        String userPw = request.getParameter("userPw");

        UserDAO dao = new UserDAO();
        boolean isValid = dao.loginCheck(userId, userPw);

        if (isValid) {
            int result = dao.deleteUser(userId);

            if (result > 0) {
                request.getSession().invalidate(); // 로그아웃 처리
                request.setAttribute("msg", "회원 탈퇴가 완료되었습니다.");
                request.setAttribute("location", request.getContextPath() + "/UserController?action=loginForm");
            } else {
                request.setAttribute("msg", "회원 탈퇴 실패. 관리자에게 문의하세요.");
                request.setAttribute("location", request.getContextPath() + "/BoardController?action=list");
            }
        } else {
            request.setAttribute("msg", "비밀번호가 일치하지 않습니다.");
            request.setAttribute("location", request.getContextPath() + "/UserController?action=deleteUserCheckForm");
        }

        RequestDispatcher rd = request.getRequestDispatcher("/memV02DAO/memV02_01_BoardV02/boardFrame/msgChk.jsp");
        rd.forward(request, response);
    }


    private void showLoginForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/memV02DAO/memV02_01_BoardV02/boardFrame/login.jsp");
        rd.forward(request, response);
    }
    
    private void showSignUpForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/memV02DAO/memV02_01_BoardV02/boardFrame/signUp.jsp");
        rd.forward(request, response);
    }

    private void showFindIdForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/memV02DAO/memV02_01_BoardV02/boardFrame/findId.jsp");
        rd.forward(request, response);
    }
    
    private void showUserEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/memV02DAO/memV02_01_BoardV02/boardFrame/userEdit.jsp");
        rd.forward(request, response);
    }

    
    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String uploadPath = getServletContext().getRealPath("/profiles");
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdir();

        try {
            MultipartRequest multi = new MultipartRequest(
                request,
                uploadPath,
                5 * 1024 * 1024,
                "UTF-8",
                new DefaultFileRenamePolicy()
            );

            String userId = multi.getParameter("userId");
            String currentPw = multi.getParameter("currentPw");  // 기존 비밀번호
            String newPw = multi.getParameter("newPw");          // 새 비밀번호
            String userEmail = multi.getParameter("userEmail");
            String deleteImg = multi.getParameter("deleteProfileImg");

            // 현재 비밀번호 검증
            UserDAO dao = new UserDAO();
            if (!dao.loginCheck(userId, currentPw)) {
                request.setAttribute("msg", "❗ 현재 비밀번호가 일치하지 않습니다.");
                request.setAttribute("location", request.getContextPath() + "/UserController?action=userEditForm");
                RequestDispatcher rd = request.getRequestDispatcher("/memV02DAO/memV02_01_BoardV02/boardFrame/msgChk.jsp");
                rd.forward(request, response);
                return;
            }

            String profileImgFileName = null;

            if ("true".equals(deleteImg)) {
                profileImgFileName = "default.png";
            } else {
                File profileImg = multi.getFile("newProfileImg");
                if (profileImg != null) {
                    String fileName = profileImg.getName();
                    String lowerFileName = fileName.toLowerCase();

                    if (lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".jpeg") ||
                        lowerFileName.endsWith(".png") || lowerFileName.endsWith(".gif") ||
                        lowerFileName.endsWith(".webp")) {
                        profileImgFileName = fileName;
                    } else {
                        profileImg.delete();  // 확장자 안맞으면 삭제
                        request.setAttribute("msg", "❗ 이미지 파일(jpg, png, gif, webp)만 업로드 가능합니다.");
                        request.setAttribute("location", request.getContextPath() + "/UserController?action=userEditForm");
                        RequestDispatcher rd = request.getRequestDispatcher("/memV02DAO/memV02_01_BoardV02/boardFrame/msgChk.jsp");
                        rd.forward(request, response);
                        return;
                    }
                }
            }

            if (profileImgFileName == null || profileImgFileName.trim().isEmpty()) {
                profileImgFileName = "default.png";
            }

            UserDTO dto = new UserDTO();
            dto.setUserId(userId);
            dto.setUserEmail(userEmail);
            dto.setProfileImg(profileImgFileName);

            if (newPw != null && !newPw.trim().isEmpty()) {
                dto.setUserPw(newPw.trim());
            }

            int result = dao.updateUserProfile(dto);

            if (result > 0) {
                UserDTO updatedUser = dao.getUser(userId);
                HttpSession session = request.getSession();
                session.setAttribute("loginUser", updatedUser);
                session.setAttribute("userId", updatedUser.getUserId());
                session.setAttribute("userName", updatedUser.getUserName());

                response.sendRedirect("BoardController?action=list");
            } else {
                request.setAttribute("msg", "수정 실패");
                request.setAttribute("location", request.getContextPath() + "/UserController?action=userEditForm");
                RequestDispatcher rd = request.getRequestDispatcher("/memV02DAO/memV02_01_BoardV02/boardFrame/msgChk.jsp");
                rd.forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "프로필 수정 중 오류 발생");
        }
    }
    
    private void showUserDeleteForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userId = (String) request.getSession().getAttribute("userId");
        request.setAttribute("userId", userId);
        request.setAttribute("category", "userDelete");  // 구분용
        RequestDispatcher rd = request.getRequestDispatcher("/memV02DAO/memV02_01_BoardV02/boardFrame/boardDelUpdChk.jsp");
        rd.forward(request, response);
    }


}//UserController
