import React, {Component} from 'react';
import './App.css';
import axios from "axios";

class App extends Component {
    render() {
        return (
            <div className="App">
                <header className="App-header">
                    <h1>Entity Viewer</h1>
                </header>
                <EntityChooser/>
                <EntityTable/>
            </div>
        );
    }
}

class EntityChooser extends Component {
    constructor(props) {
        super(props);
        this.state = {entities: []};
    }

    componentDidMount() {
        axios
            .get("http://localhost:8080/entity/repository/")
            .then(response => {

                this.setState({entities: response.data});
            })
    }

    render() {
        return (
            <div className="dropdown">
                <button className="btn btn-default dropdown-toggle" type="button" id="entitiesDropdown"
                        data-toggle="dropdown"
                        aria-haspopup="true" aria-expanded="false">
                    Available Entities
                    <span className="caret"></span>
                </button>
                <ul className="dropdown-menu" aria-labelledby="entitiesDropdown">
                    {this.state.entities.map(function (d, idx) {
                        return (<li id={d}><a href="#" onClick={() => this.props.handler(d)}>{d}</a></li>)
                    })}
                </ul>
            </div>
        )
    }
}

class EntityTable extends Component {
    constructor(props) {
        super(props);
        var entityData = [{a: "1", b: "2", c: "3"}, {a: "4", b: "5", c: "6"}]
        this.state = {entity: "test", columns: ["a", "b", "c"], entityData: entityData};
        console.log("constrcutor state: " + this.state);
    }

    render() {
        return (
            <table className="table">
                <tr>
                    {this.state.columns.map((d) => <th>{d}</th>)}
                    <th>
                        <span className="fa fa-tasks"></span>
                    </th>
                </tr>
                {this.state.entityData.map((row) =>
                    <tr>
                        {this.state.columns.map((columnName) => <td>{row[columnName]}</td>)}
                        <td>
                            <div className="btn-group" role="group" aria-label="Edit section">
                                <button type="button" className="btn btn-primary"><span
                                    className="fa fa-pencil"></span></button>
                                <button type="button" className="btn btn-danger"><span
                                    className="fa fa-trash"></span></button>
                            </div>
                        </td>
                    </tr>)}
            </table>
        )
    }

}


export default App;
