package main

import (
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
)

func handler(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, "{\"segments\":["+
		"{\"name\":\"Test\",\"rules\":[],\"id\":\"148b454a-1c25-448b-80a9-4d416d2efaf6\"},"+
		"{\"name\":\"Demo\",\"rules\":[],\"id\":\"148b454a-1c25-448b-80a9-asdf24f23f3f\"}"+
		"],\"status\":\"ok\"}")
}

func userInformation(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, "{\"user\" : {\"segments\" : [\"148b454a-1c25-448b-80a9-4d416d2efaf6\"]}, \"status\" : \"ok\"}")
	//fmt.Fprintf(w, "{\"user\" : {\"segments\" : [\"wasanderes\"]}, \"status\" : \"ok\"}")
	//fmt.Fprintf(w, "{\"user\" : {\"segments\" : [\"\"]}}")
}

func audience(w http.ResponseWriter, r *http.Request) {
	if r.Method == http.MethodDelete {
		fmt.Println("deleted " + r.URL.RawQuery)
		fmt.Fprintf(w, "{\"status\" : \"ok\"}")
	} else if r.Method == http.MethodPost {
		fmt.Println("created " + r.URL.RawQuery)
		fmt.Fprintf(w, "{\"status\" : \"ok\"}")
	}
}

func webTools(w http.ResponseWriter, r *http.Request) {

	dat, _ := ioutil.ReadFile("webtools-tracking.js")
	fmt.Fprintf(w, string(dat))
}

func main() {
	http.HandleFunc("/rest/segments/all", handler)
	http.HandleFunc("/rest/userinformation/user", userInformation)
	http.HandleFunc("/rest/audience", audience)
	http.HandleFunc("/js/webtools.js", webTools)
	log.Fatal(http.ListenAndServe(":8082", nil))
}
