import axios from "axios";
import { Component } from "react";

class ApiCall extends Component {
    constructor(props) {
        super(props);
        this.state = {
            indexType: props.indexType,
            query: props.query,
            response: null
        };
        this.postApiCall()
        // this.getApiCall();
    }
    componentDidUpdate(prevProps, prevState) {
        if (prevState.response !== this.state.response ||
            prevState.query !== this.state.query) {
            document.getElementsByClassName("responseText")[0].value = this.state.response;
        }
    }
    postApiCall = () => {
        console.log("Sending POST request from Search Engine" + this.state.indexType)
        let indexType = this.state.indexType;
        // Send a POST request
        axios({
            method: 'post',
            url: 'http://localhost:8080/webcrawler-0.0.1/' + indexType,
            data: this.state.query
        }).then((response) => {
            console.log("Recieved response from server Java function");
            console.log(response.data);
            this.setState({ response: response.data });
        })
    }
    getApiCall = () => {
        console.log("Sending POST request from Search Engine" + this.state.indexType)
        let indexType = this.state.indexType
        // GET request
        axios('http://localhost:8080/webcrawler-0.0.1/' + indexType)
            .then((response) => {
                console.log("Recieved response from server Java function");
                this.setState({ response: response.data });
            });
    }

    render() {
        return (null);
    }
}
export default ApiCall;