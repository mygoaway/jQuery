/**
 * 회원가입처리용 jQuery
 * 작성날짜:2021.3.9
 * 작성자:홍길동
 */

$(function(){
	//에러메세지는 처음에는 안보이게 설정(show()<->hide())
	$('#id_error').hide()
	$('#age_error1').hide()
	$('#age_error2').hide()
	$('#p_error1').hide()
	$('#p_error2').hide()
	//<input type="button"  id="btnSend" value="전송">
	$('#btnSend').click(function(){
		//1.id입력 체크
		var id=$('#userid').val()
		if(id.length < 1){
			$('#id_error').show()
			return false;
		}else{//한글자이상 입력  else if(id.length >=4){ //4글자이상
			$('#id_error').hide()
		}
		//2.age입력체크
		var age=$('#age').val()
		if(age.length < 1){
			$('#age_error1').show()
			return false;
		}else{//한글자이상 입력
			$('#age_error1').hide()
		}
		//3.숫자인지 체크?->a2 or a,2a3->문자포함X ->isNaN함수
		//0(48)~9(57) 범위를 벗어나면 무조건 문자
		for(var i=0;i<age.length;i++){
			var data=age.charAt(i).charCodeAt(0)//아스키코드값으로 변환
			//alert(data) //48~57
			if(data < 48  || data > 57){//문자라면
				$('#age_error2').show() //숫자입력하세요
				return false;
				break;//문자를 발견하면 더이상 체크할 필요X
			}else{//숫자를 입력했다면
				$('#age_error2').hide()
			}
		}
		
		//4.pwd입력
		var pwd1=$('#pwd1').val()
		if(pwd1.length < 1){
			$('#p_error1').show()
			return false;
		}else{//한글자이상 입력
			$('#p_error1').hide()
		}
		
		//5.pwd불일치 체크
		var pwd2=$('#pwd2').val()
		if(pwd2.length < 1){
			$('#p_error2').show()
			return false;
		}else{//한글자이상 입력
			$('#p_error2').hide()
		}
		//불일치체크
		if(pwd1!=pwd2){
			$('#p_error2').show() //불일치하다.
		}
		//<form id="signup" method="post" action="register.jsp">
		//정상적으로 다입력했다면 document.form객체명.submit()->action="register.jsp"
		$('#signup').attr('action','register.jsp').submit()
		return true;//전송이 가능하게 설정
		
	})
})








