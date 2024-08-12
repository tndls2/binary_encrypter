[📚 javascript 베이스캠프](https://www.books.weniv.co.kr/basecamp-javascript)   


## JavaScript
웹페이지에서 동작을 구현할 때 사용되는 `프로그래밍 언어`
- HTML, CSS를 프로그래밍적으로 제어 가능
- 동적인(live) 웹 지향
   - 데이터 저장 
    - 값 계산
    - 결과 반영 : `DOM API`(Document Objecct Model), `BOM API`(Browser Object Model)
    - 다른 컴퓨터와 통신 : `AJAX`
> **바닐라 자바스크립트**🍦
    : 순수 자바스크립트  

> **BOM(브라우저 객체 모델)** : JavaScript가 브라우저와 소통하기 위한 도구들을 제공하는 것 ex) `console`

### let vs const
1. `let`   
    - 변수의 가리키는 값이 `반드시 변경`되어야 하는 경우 사용

2. `const`  
    - 의도하지 않은 값의 변경을 방지 가능
    - 가독성 및  유지 보수성 ⬆️ : 다른 개발자들이 변수의 값이 변경될 가능성이 없다는 것을 빠르게 인지 가능

> tip: const로 선언하고, 문제 생기면 let으로 변경하기!

### undefined vs null
1. `undefined`
    - 변수가 값을 갖고 있지 않음 = 값이 할당되지 않음
2. `null`
    - 값이 없음 = 의도적으로 값이 없음
> **값이 비어있음을 나타내기 위해 `undefined`를 사용해도 될까? 🤔**  
    의미적으로는 부적절함! 하지만 동작상 상관없음  
     자바스크립트의 메모리를 관리해주는 프로그램인 `가비지 컬렉터`가 null과 undefined를 구분하지 않기 때문


### 템플릿 리터럴
`문자열 안에 변수를 삽입`할 수 있도록 해주는 문자열 메소드 
- 백틱(`)으로 문자열 감쌈
- `${}`안에 변수 넣음 + 연산도 가능

```javascript
let num1 = 10;
let num2 = 20;
console.log(`두 수의 합은 ${num1 + num2}입니다.`);

let num = 3;
console.log(`증감연산 : ${++num}`); // 증감연산자도 사용가능

console.log(`비교연산 : ${2 === 3}`); // 비교연산자도 사용가능 (결과값 : true / false)
```

### == vs ===
- `==` : 값만 비교
- `===` : 값 + 타입까지 비교  
-> `===`를 사용하는 것이 더 안전

```javascript
2 == '2'; // true 
2 === '2'; // false
```

### 형변환
1. 문자열 -> 숫자로 변환  
    - `parseInt` : 정수로 변환
    - `parseFloat` : 실수로 변환

    ```javascript
    parseInt('hello'); // NaN : 문자열     'hello'는 숫자로 변환 불가
    parseInt('30b'); // 30 : 문자열 '30b'에서 숫자 부분만 변환
    parseFloat('45.4'); // 45.4
    ```

2. 숫자 -> 문자 형변환
    - toString() : 진수 변환 가능
      
    ```javascript
    let num = 5;
    typeof num.toString(); // string : 숫자 5를 문자열 '5'로 변환
    (3).toString(); // '3' :숫자 3을 문자열 '3'로 변환
    (3).toString(2); // '11' : 숫자 3을 2진수로 변환하여 '11'로 표현
    ```
  > 🚨 `3.toString()`은 에러 발생   
       : 자바스크립트 엔진은 숫자 뒤의 점을 소수점으로 인식하기 때문  
      -> 소괄호로 감싸기!
### Number.isNaN()
값이 NaN인지 확인  
`isNan()`보다 더 정확하게 NaN 사용하고 판별함

```javascript
isNaN(undefined); // true : undefined는 숫자로 변환 불가
Number.isNaN(undefined); // false : undefined는 NaN이 아님
Number.isNaN(NaN); // true (NaN은 NaN)
```

### Parameter vs Argument
| 용어 | 번역 | 의미 |
| --- | --- | --- |
| Parameter | 매개변수 | 함수와 메서드에 입력 변수 이름 **(선언)** |
| Argument | 전달인자, 인자, 인수 | 함수와 메서드에 실제 입력되는 값 **(실제값)** |

### 화살표 함수
`function` 키워드 대신 `=>`로 함수 표현  
함수의 이름이 없음!  

```javascript
// 일반 함수
function add(x, y) {
  return x + y;
}

// 화살표 함수
let 더하기함수 = (x, y) => x + y;
```

### forEach()
배열의 각 요소에 대해 주어진 함수를 실행하는 메소드  
함수는 인자로 `배열 요소, 인덱스`를 받음 -> 배열의 요소를 순회하면서 해당 요소를 함수로 전달

```javascript
// 화살표 함수로 많이 표현함
const arr = ['a',  'b', 'c'];
arr.forEach((item, index) => {
  console.log(item + index);
});
```

### map()
배열의 각 요소에 대해 주어진 함수 실행  
-> `새로운 배열`로 반환하는 메소드   
배열 안에 객체에서 데이터를 뽑는 형태로도 사용  
```javascript
const ages = data.map((item) => item['name']);
```

### join()
배열의 모든 요소를 연결하여 `하나의 문자열`로 만드는 메소드
연결할 때 사용할 문자열을 인자로 받음  
```javascript
const arr = ['hello', 'world'];
console.log(arr1.join('')); // helloworld
```

