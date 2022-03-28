import logo from './searchItem.png'
import './App.css';
import APICall from './APICall';
import { Component } from 'react';

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      indexTypeSelected: "lucene",
      query: "",
      submitClicked: false
    };
  }
  componentDidUpdate(prevProps, prevState) {
    if (this.state.submitClicked === true && (prevState.indexTypeSelected !== this.state.indexTypeSelected ||
      prevState.query !== this.state.query)) {
      console.log("radio changed")
      this.setState({
        submitClicked: false
      })
    }
  }

  handleClick = (event) => {
    this.setState({
      submitClicked: true
    });
  }

  handleTextChange = (event) => {
    const value = event.target.value;
    console.log(value);
    this.setState({
      query: value
    })
  }

  handleRadioChange = (event) => {
    const value = event.target.className;
    console.log(value);
    if (value === "lucene") {
      this.setState({
        indexTypeSelected: "lucene"
      });
    }
    else {
      this.setState({
        indexTypeSelected: "hadoop"
      });
    }
  }

  clearTextArea = () => {
    document.getElementsByClassName("responseText")[0].value = "";
  }

  render() {
    return (
      <div className="App">
        <header className="App-header">
          <label>Search Engine</label>
          <br />
          <img src={logo} className="App-logo" alt="logo" />
        </header>

        <label className='searchLabel'>Search: </label>
        <input type="text" className='searchBox' autoFocus={true} onChange={this.handleTextChange} />
        <br />
        <label className='indexHeadingLabel'>Select the indexing Method:</label>
        <input
          type='radio'
          name='radioIndex'
          className='lucene'
          defaultChecked
          onChange={this.handleRadioChange}
        />
        <label>Lucene</label>
        <input
          type='radio'
          name='radioIndex'
          className='hadoop'
          onChange={this.handleRadioChange} />
        <label>Hadoop</label>
        <br />
        <input type='submit' className='searchSubmit' onClick={this.handleClick} />
        {
          this.state.submitClicked &&
          <APICall indexType={this.state.indexTypeSelected} query={this.state.query} />
        }
        <br />
        <textarea className='responseText' cols="150" rows="20" readOnly={true}></textarea><br />
        <button type='reset' className='clearTextArea' onClick={this.clearTextArea}>Clear</button>
      </div>
    );
  }
}


export default App;
